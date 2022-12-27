package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.dto.PageDTO;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.Spu;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuService spuService;

    @Autowired
    private SpuDetailService detailService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpecParamService paramService;


    @Override
    public PageDTO<SpuDTO> spuPageQuery(Integer page, Integer rows, Long cid, Long bid, Boolean saleable, Long id) {

        IPage<Spu> iPage = new Page<>(page, rows);
        //先分页查询spu,并且整合动态sql查询
        this.spuService.page(iPage, new QueryWrapper<Spu>()
                .eq(null != cid, "cid3", cid)
                .eq(null != bid, "brand_id", bid)
                .eq(saleable != null, "saleable", saleable)
                .eq(null != id, "id", id)
        );

        //spuEntity==>spuDTO
        List<SpuDTO> spuDTOS = SpuDTO.convertEntityList(iPage.getRecords());

        //循环给dto赋值，brandName以及categoryName
        spuDTOS.forEach(spuDTO -> {
            spuDTO.setBrandName(this.brandService.getById(spuDTO.getBrandId()).getName());

            //TODO 考虑缓存问题，
            //根据分类的id集合查询分类集合，并取出每个分类的名称，并拼接字符串
            String names = this.categoryService.listByIds(spuDTO.getCategoryIds())
                    .stream().map(Category::getName)
                    .collect(Collectors.joining("/"));
            //StringUtils.join(array,分隔符)
            spuDTO.setCategoryName(names);
        });

        return new PageDTO<SpuDTO>(iPage.getTotal(), iPage.getPages(), spuDTOS);
    }

    @Override
    @Transactional
    public void addGoods(SpuDTO spuDTO) {

        if (CollectionUtils.isEmpty(spuDTO.getSkus())) {
            throw new LyException(400, "sku不能为空");
        }


        //保存spu，并主键回显
        Spu spu = spuDTO.toEntity(Spu.class);
        this.spuService.save(spu);
        log.info("保存spu成功");

        //保存spuDetail

        SpuDetail spuDetail = spuDTO.getSpuDetail().toEntity(SpuDetail.class);

        //手动指派spuId
        spuDetail.setSpuId(spu.getId());

        this.detailService.save(spuDetail);

        log.info("保存spuDetail成功");

        //获取到是skuDTO集合，转为sku的集合，并且给每个sku设置spu_id以及saleable为true

        List<Sku> skus = spuDTO.getSkus().stream().map(skuDTO -> {
            Sku sku = skuDTO.toEntity(Sku.class);
            sku.setSpuId(spu.getId());
            sku.setSaleable(true);
            return sku;
        }).collect(Collectors.toList());

        this.skuService.saveBatch(skus);

        log.info("批量保存sku成功");
    }

    @Autowired
    private AmqpTemplate amqpTemplate;


    @Override
    @Transactional
    public void modifySaleable(Long id, Boolean saleable) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(saleable);

        //update tb_spu set saleable = false where id = 2;
        this.spuService.updateById(spu);

        Sku sku = new Sku();
        sku.setSaleable(saleable);

        //update tb_sku set saleable = false where spu_id = 2;
        this.skuService.update(sku, new QueryWrapper<Sku>().eq("spu_id", id));


        String routingKey = saleable ? "item.up" : "item.down";

        //交换机，routingKey，消息,spuId
        this.amqpTemplate.convertAndSend("jhj", routingKey, id);
    }

    @Override
    public SpuDTO queryGoodsById(Long id) {

        PageDTO<SpuDTO> spuDTOPageDTO = this.spuPageQuery(1, 1, null, null, null, id);

        List<SpuDTO> items = spuDTOPageDTO.getItems();
        if (null == items || 0 == items.size()) {
            throw new LyException(204, "此id查询对应信息为空");
        }

        SpuDTO spuDTO = items.get(0);

        //select * from tb_spu_detail where spu_id = #{id}
        spuDTO.setSpuDetail(new SpuDetailDTO(this.detailService.getById(id)));

        //select * from tb_sku where spu_id = #{id}
        spuDTO.setSkus(SkuDTO.convertEntityList(this.skuService.query().eq("spu_id", id).list()));


        return spuDTO;
    }

    @Override
    @Transactional
    public void updateGoods(SpuDTO spuDTO) {
        Long id = spuDTO.getId();

        //id不为空说明spu有修改需求
        if (null != id) {
            this.spuService.updateById(spuDTO.toEntity(Spu.class));
        }
        //detail不为空说明detail有修改需求
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        if (null != spuDetailDTO) {
            this.detailService.updateById(spuDetailDTO.toEntity(SpuDetail.class));
        }


        List<SkuDTO> skuDTOS = spuDTO.getSkus();

        //sku的集合不为空说明有修改或删除或新增需求
        if (null != skuDTOS && 0 != skuDTOS.size()) {

            //两个实体，key为true表示要修改或者新增，false，表示要删除
            Map<Boolean, List<Sku>> skuMap = skuDTOS
                    .stream()
                    .map(skuDTO -> skuDTO.toEntity(Sku.class))
                    .collect(Collectors.groupingBy(sku -> sku.getSaleable() == null));

            List<Sku> toBeAddOrUpdate = skuMap.get(true);

            if (!CollectionUtils.isEmpty(toBeAddOrUpdate)) {
                //批量修改或新增sku
                this.skuService.saveOrUpdateBatch(toBeAddOrUpdate);
            }


            List<Sku> toBeDelete = skuMap.get(false);

            if (!CollectionUtils.isEmpty(toBeDelete)) {

                //delete from tb_sku where id in (xxx,xxx,xxxx)
                this.skuService.removeByIds(toBeDelete.stream().map(Sku::getId).collect(Collectors.toList()));
            }
        }
    }

    @Override
    public List<SkuDTO> listSkuByIds(List<Long> ids) {

        return SkuDTO.convertEntityList(this.skuService.listByIds(ids));
    }

    @Override
    public List<SkuDTO> listSkuBySpu(Long id) {

        return SkuDTO.convertEntityList(this.skuService.query().eq("spu_id", id).list());
    }

    @Override
    public SpuDetailDTO querySpuDetailById(Long spuId) {
        return new SpuDetailDTO(this.detailService.getById(spuId));
    }

    @Override
    public SpuDTO querySpuById(Long id) {
        return new SpuDTO(this.spuService.getById(id));
    }

    @Override
    public List<SpecParamDTO> querySpecParamValue(Long id, Boolean searching) {

        Long categoryId = this.spuService.getById(id).getCid3();

        List<SpecParamDTO> specParamDTOS = this.paramService.listParam(null, categoryId, searching);

        String specification = this.detailService.getById(id).getSpecification();


        //把规格属性从json转换为map，key为long即规格参数的id，value，就是规格参数的值
        Map<Long, Object> paramMap = JsonUtils.nativeRead(specification, new TypeReference<Map<Long, Object>>() {
        });


        specParamDTOS.forEach(specParamDTO -> specParamDTO.setValue(paramMap.get(specParamDTO.getId())));

        return specParamDTOS;
    }

    @Override
    @Transactional
    public void minusStock(Map<Long, Integer> cartsMap) {
        this.skuService.minusStock(cartsMap);
    }

    @Transactional
    @Override
    public void plusStock(Map<Long, Integer> detailsMap) {
        this.skuService.plusStock(detailsMap);
    }
}
