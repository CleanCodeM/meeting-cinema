package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.Vo.CinemaInfoVo;
import com.stylefeng.guns.rest.Vo.FilmFieldVo;
import com.stylefeng.guns.rest.Vo.HallFilmInfoVo;
import com.stylefeng.guns.rest.cinema.CinemaService;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallFilmInfoTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallFilmInfoT;
import com.stylefeng.guns.rest.vo.BaseReqVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 22:16
 */
@Component
@Service(interfaceClass = CinemaService.class)
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;

    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;

    @Autowired
    MtimeHallFilmInfoTMapper mtimeHallFilmInfoTMapper;

    //获取播放场次
    @Override
    public BaseReqVo getFields(Integer cinemaId) {
        BaseReqVo<Map> baseReqVo = new BaseReqVo<>();
        HashMap<String, Object> dataMap = new HashMap<>();
        //查询该电影院信息
        MtimeCinemaT cinema = mtimeCinemaTMapper.selectById(cinemaId);
        CinemaInfoVo cinemaInfo = conver2CinemaInfoVo(cinema);
        dataMap.put("cinemaInfo", cinemaInfo);
        EntityWrapper<MtimeFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("cinema_id", cinemaId);
        //查询该电影院的所有场次
        List<MtimeFieldT> mtimeFieldTList = mtimeFieldTMapper.selectList(wrapper);
        //所有的场次中有哪些电影
        HashSet<Integer> filmIds = new HashSet<>();
        for (MtimeFieldT mtimeFieldT : mtimeFieldTList) {
            filmIds.add(mtimeFieldT.getFilmId());
        }
        ArrayList<Object> filmList = new ArrayList<>();
        //获取场次电影详情
        ArrayList<MtimeHallFilmInfoT> mtimeHallFilmInfoTS = new ArrayList<>();
        for (Integer filmId : filmIds) {
            MtimeHallFilmInfoT mtimeHallFilmInfoTForSelect = new MtimeHallFilmInfoT();
            mtimeHallFilmInfoTForSelect.setFilmId(filmId);
            MtimeHallFilmInfoT mtimeHallFilmInfoT = mtimeHallFilmInfoTMapper.selectOne(mtimeHallFilmInfoTForSelect);
            HallFilmInfoVo hallFilmInfoVo = conver2FilmInfoVo(mtimeHallFilmInfoT);
            ArrayList<FilmFieldVo> filmFieldVos = new ArrayList<>();
            for (MtimeFieldT mtimeFieldT : mtimeFieldTList) {
                if (filmId == mtimeFieldT.getFilmId()){
                    FilmFieldVo filmFieldVo = conver2FilmFieldVo(mtimeFieldT);
                    filmFieldVo.setLanguage(mtimeHallFilmInfoT.getFilmLanguage());
                    filmFieldVos.add(filmFieldVo);
                }
            }
            hallFilmInfoVo.setFilmFields(filmFieldVos);
            filmList.add(hallFilmInfoVo);
        }
        dataMap.put("filmList", filmList);
        baseReqVo.setData(dataMap);
        baseReqVo.setImgPre("http://img.meetingshop.cn/");
        baseReqVo.setStatus(0);
        return baseReqVo;
    }

    private FilmFieldVo conver2FilmFieldVo(MtimeFieldT mtimeFieldT) {
        FilmFieldVo filmFieldVo = new FilmFieldVo();
        if (mtimeFieldT == null){
            return filmFieldVo;
        }
        BeanUtils.copyProperties(mtimeFieldT, filmFieldVo);
        filmFieldVo.setPrice(Integer.toString(mtimeFieldT.getPrice()));
        filmFieldVo.setFieldId(mtimeFieldT.getUuid());
        return filmFieldVo;
    }

    private HallFilmInfoVo conver2FilmInfoVo(MtimeHallFilmInfoT mtimeHallFilmInfoT) {
        HallFilmInfoVo hallFilmInfoVo = new HallFilmInfoVo();
        if (mtimeHallFilmInfoT == null){
            return hallFilmInfoVo;
        }
        BeanUtils.copyProperties(mtimeHallFilmInfoT, hallFilmInfoVo);
        hallFilmInfoVo.setFilmType(mtimeHallFilmInfoT.getFilmLanguage());
        return hallFilmInfoVo;
    }

    private CinemaInfoVo conver2CinemaInfoVo(MtimeCinemaT cinema) {
        CinemaInfoVo cinemaInfo = new CinemaInfoVo();
        if (cinema == null){
            return cinemaInfo;
        }
        BeanUtils.copyProperties(cinema, cinemaInfo);
        cinemaInfo.setCinemaAdress(cinema.getCinemaAddress());
        cinemaInfo.setCinemaId(cinema.getUuid());
        cinemaInfo.setImgUrl(cinema.getImgAddress());
        return cinemaInfo;
    }
}
