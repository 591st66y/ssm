package com.coderman.web;

import com.coderman.common.JsonData;
import com.coderman.dto.form.DeptParam;
import com.coderman.dto.form.UserAddParam;
import com.coderman.dto.form.UserUpdateParam;
import com.coderman.exception.ParamException;
import com.coderman.model.Dept;
import com.coderman.model.User;
import com.coderman.service.DeptService;
import com.coderman.util.BeanValidator;
import com.coderman.util.TreeObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author zhangyukang
 * @Date 2020/8/27 16:35
 * @Version 1.0
 **/
@Controller
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @RequestMapping(value = "/tree.do",method = RequestMethod.POST)
    @ResponseBody
    public List<TreeObject> tree(){
        return deptService.tree();
    }

    @RequestMapping(value = "/get.do", method = RequestMethod.GET)
    @ResponseBody
    public JsonData get(@RequestParam("id") Long id) {
        Dept dept = deptService.getById(id);
        return JsonData.success(dept);
    }

    @RequestMapping(value = "/add.do", method = RequestMethod.POST)
    @ResponseBody
    public JsonData add(DeptParam deptParam) throws ParamException {
        BeanValidator.check(deptParam);
        deptService.add(deptParam);
        return JsonData.success();
    }

    @RequestMapping(value = "/delete.do", method = RequestMethod.POST)
    @ResponseBody
    public JsonData delete(@RequestParam("id") Long id){
        deptService.delete(id);
        return JsonData.success();
    }


    @RequestMapping(value = "/update.do", method = RequestMethod.POST)
    @ResponseBody
    public JsonData update(DeptParam deptParam) throws ParamException {
        BeanValidator.check(deptParam);
        deptService.update(deptParam);
        return JsonData.success();
    }

}
