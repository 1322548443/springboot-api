package com.company.project.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.utils.JwtUtils;
import com.company.project.utils.MD5Utils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.company.project.service.IUserService;
import com.company.project.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author project
 * @since 2020-01-08
 */
@Api(tags = {""})
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private IUserService userService;

    @ApiOperation("登陆")
    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return ResultGenerator.genFailResult("账号未找到");
        }
        if (!MD5Utils.Encrypt(password,true).equals(user.getPassword())) {
            return ResultGenerator.genFailResult("密码错误");
        }
        String token = JwtUtils.geneJsonWebToken(user);
        user.setToken(token);
        user.setPassword("");
        return ResultGenerator.genSuccessResult(user);
    }


    @ApiOperation(value = "新增")
    @PostMapping("add")
    public Result add(@RequestBody User user){
        userService.save(user);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "删除")
    @PostMapping("delete")
    public Result delete(@PathVariable("id") Long id){
        userService.removeById(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "更新")
    @PostMapping("update")
    public Result update(@RequestBody User user){
        userService.updateById(user);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation(value = "查询分页数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "currentPage", value = "页码"),
        @ApiImplicitParam(name = "pageCount", value = "每页条数")
    })
    @GetMapping("listByPage")
    public Result findListByPage(@RequestParam Integer currentPage,
                                   @RequestParam Integer pageCount){
        Page page = new Page(currentPage, pageCount);
        IPage<User> iPage = userService.page(page);
        return ResultGenerator.genSuccessResult(iPage);
    }

    @ApiOperation(value = "id查询")
    @GetMapping("getById")
    public Result findById(@PathVariable Long id){
        return ResultGenerator.genSuccessResult(userService.getById(id));
    }

}
