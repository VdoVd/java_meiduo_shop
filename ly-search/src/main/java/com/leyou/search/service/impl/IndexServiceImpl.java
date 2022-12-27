package com.leyou.search.service.impl;

import com.leyou.common.dto.PageDTO;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private GoodsRepository goodsRepository;

    //1,从item服务查询spu数据,所有



    @Override
    public void loadData() {

         int page = 1;


        //索引库删除
        try {
            this.goodsRepository.deleteIndex();
            log.info("索引库删除成功");
        } catch (Exception e) {
            log.info("没有索引库要删除");
        }


        //索引库创建
        this.goodsRepository.createIndex("{\n" +
                "  \"settings\": {\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"my_pinyin\": {\n" +
                "          \"tokenizer\": \"ik_smart\",\n" +
                "          \"filter\": [\n" +
                "            \"py\"\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"filter\": {\n" +
                "        \"py\": {\n" +
                "\t\t  \"type\": \"pinyin\",\n" +
                "          \"keep_full_pinyin\": true,\n" +
                "          \"keep_joined_full_pinyin\": true,\n" +
                "          \"keep_original\": true,\n" +
                "          \"limit_first_letter_length\": 16,\n" +
                "          \"remove_duplicated_term\": true\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"id\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"suggestion\": {\n" +
                "        \"type\": \"completion\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"title\":{\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"my_pinyin\",\n" +
                "        \"search_analyzer\": \"ik_smart\"\n" +
                "      },\n" +
                "      \"image\":{\n" +
                "        \"type\": \"keyword\",\n" +
                "        \"index\": false\n" +
                "      },\n" +
                "      \"updateTime\":{\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"specs\":{\n" +
                "        \"type\": \"nested\",\n" +
                "        \"properties\": {\n" +
                "          \"name\":{\"type\": \"keyword\" },\n" +
                "          \"value\":{\"type\": \"keyword\" }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}");


        log.info("索引库创建成功");
        log.info("开始导入数据");


        while (true) {
            PageDTO<SpuDTO> spuDTOPageDTO = this.itemClient.spuPageQuery(page, 50, null, null, true, null);

            //第一页都没有数据
            if (0 == spuDTOPageDTO.getTotalPage()) {
                break;
            }


            //有数据要被获取,把获取到的spuDTO的集合转换为goods集合
            List<Goods> goodsList = spuDTOPageDTO
                    .getItems()
                    .stream()
                    .map(this::buildGoods)
                    .collect(Collectors.toList());

            this.goodsRepository.saveAll(goodsList);

            page++;
            //当下一次要查询的页码比总页码多，就停
            if (page > spuDTOPageDTO.getTotalPage()) {
                break;
            }
        }
    }

    //2,把查询到的数据转换为goods，（先创建索引库）然后保存到索引库

    @Override
    public Goods buildGoods(SpuDTO spuDTO) {
        Goods goods = new Goods();

        //相同名称以及类型的属性可以直接对拷，title除外
        BeanUtils.copyProperties(spuDTO, goods);

        CategoryDTO categoryDTO = this.itemClient.queryCategoryById(spuDTO.getCid3());

        String categoryName = categoryDTO.getName();

        BrandDTO brandDTO = this.itemClient.queryBrandById(spuDTO.getBrandId());

        //要(之前的内容
        String brandName = StringUtils.substringBefore(brandDTO.getName(), "（");

        String title = null;

        //goods中的title属性要包含，分类，以及品牌的信息，这样没有直接写商品名称，写了分类或者品牌名称也能搜索到当前商品
        title = spuDTO.getName() + (spuDTO.getName().contains(categoryName) ? "" : categoryName)
                + (spuDTO.getName().contains(brandName) ? "" : brandName);

        goods.setTitle(title);

        //自动补全候选内容，分别包含，品牌，分类，品牌+分类，分类+品牌，商品名称，品牌+商品名称
        List<String> suggs = new ArrayList<>();
        suggs.add(brandName);
        suggs.add(categoryName);
        suggs.add(brandName + categoryName);
        suggs.add(categoryName + brandName);
        suggs.add(spuDTO.getName());
        suggs.add(brandName + spuDTO.getName());


        goods.setSuggestion(suggs);


        //categoryId
        goods.setCategoryId(spuDTO.getCid3());


        List<SkuDTO> skuDTOS = this.itemClient.listSkuBySpu(spuDTO.getId());
        //image

        //从spu对应的sku的集合中随意取一个，然后，取其中一张图片，多张图片以,逗号分隔，切分展示
        goods.setImage(StringUtils.substringBefore(skuDTOS.get(0).getImages(), ","));

        Set<Long> price = new HashSet<>();

        long sold = 0;

        for (SkuDTO skuDTO : skuDTOS) {
            price.add(skuDTO.getPrice());
            sold += skuDTO.getSold();
        }

        //prices
        goods.setPrices(price);
        //sold
        goods.setSold(sold);

        //updateTime
        goods.setUpdateTime(new Date());

        //specs，所有可搜索过滤规格参数名称以及对应的值
        List<Map<String,Object>> specsResult = new ArrayList<>();

        //获取所有的可搜索规格参数信息的属性名称以及对应的值，
        List<SpecParamDTO> specParamDTOS = this.itemClient.querySpecParamValue(spuDTO.getId(), true);


        //遍历规格参数集合，
        specParamDTOS.forEach(specParamDTO -> {
            Map<String,Object> result = new HashMap<>();
            //name，规格参数的名称，value，规格参数的值
            result.put("name",specParamDTO.getName());
            //保存值应该给可以进行区间处理的值，划分区间
            result.put("value",chooseSegment(specParamDTO));

            specsResult.add(result);
        });

        goods.setSpecs(specsResult);

        return goods;
    }

    @Override
    public void deleteById(Long id) {
        this.goodsRepository.deleteById(id);
    }

    @Override
    public void createIndexById(Long id) {


        //spuDTO-->Goods
        //根据spuId查询对应的spu信息
        PageDTO<SpuDTO> spuDTOPageDTO = this.itemClient.spuPageQuery(1, 1, null, null, true, id);

        //查到商品上架处理
        if (spuDTOPageDTO.getTotalPage()==1){
            SpuDTO spuDTO = spuDTOPageDTO.getItems().get(0);
            Goods goods = buildGoods(spuDTO);

            this.goodsRepository.save(goods);
        }


    }

    private Object chooseSegment(SpecParamDTO p) {
        Object value = p.getValue();
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        if (!p.getNumeric() || StringUtils.isBlank(p.getSegments()) || value instanceof Collection) {
            return value;
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }
}
