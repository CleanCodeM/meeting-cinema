package com.stylefeng.guns.rest.modular.cinema;

import com.stylefeng.guns.rest.film.vo.BaseReqVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lei.ma
 * @version 1.0
 * @date 2019/11/28 21:23
 */
@RestController
@RequestMapping("cinema/")
public class CinemaController {


    @RequestMapping("getFields")
    public BaseReqVo getFields(Integer cinemaId){
        return null;
    }
}
