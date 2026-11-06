package org.example.ljh.ciyuan.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.example.ljh.ciyuan.entity.Collection;
import org.example.ljh.ciyuan.mapper.CollectionMapper;
import org.example.ljh.ciyuan.service.CollectionService;
import org.springframework.stereotype.Service;

@Service
public class CollectionServiceImp extends ServiceImpl<CollectionMapper,Collection> implements CollectionService {

}
