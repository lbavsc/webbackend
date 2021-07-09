package com.four.webbackend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.four.webbackend.exception.BusinessException;
import com.four.webbackend.model.ResultEntity;
import com.four.webbackend.util.ResultUtil;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.imageio.IIOException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lbavsc
 * @version 1.0
 * @className GlobalExceptionHandler
 * @description
 * @date 2021/7/5 下午2:37
 **/
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 捕捉图片错误异常
     */
    @ExceptionHandler(IIOException.class)
    public ResultEntity handle403IoException() {
        return ResultUtil.error(403, "图片类型错误,请更换图片,上传图片时请勿直接修改后缀上传");
    }


    /**
     * 捕捉参数错误异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultEntity handle403(IllegalArgumentException e) {
        return ResultUtil.error(403, e.getMessage());
    }


    /**
     * 捕捉所有Shiro异常
     */
    @ExceptionHandler(ShiroException.class)
    public ResultEntity handle401(ShiroException e) {
        return ResultUtil.error(401, "无权访问(Unauthorized):" + e.getMessage());
    }

    /**
     * 单独捕捉Shiro(UnauthorizedException)异常 该异常为访问有权限管控的请求而该用户没有所需权限所抛出的异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResultEntity handle401UnauthorizedException() {
        return ResultUtil.error(401, "无权访问(Unauthorized)IIOException");
    }

    /**
     * 单独捕捉Shiro(UnauthenticatedException)异常
     * 该异常为以游客身份访问有权限管控的请求无法对匿名主体进行授权，而授权失败所抛出的异常
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ResultEntity handle401UnauthenticatedException() {
        return ResultUtil.error(401, "无权访问(Unauthorized):当前Subject是匿名Subject，请先登录(This subject is anonymous.)");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResultEntity handle413MaxUploadSizeExceededException() {

        return ResultUtil.error(413, "上传文件大小超过限制, 单文件最大为3M");
    }

    @ExceptionHandler(AccountException.class)
    public ResultEntity handle401(AccountException e) {
        return ResultUtil.error(401, e.getMessage());
    }

    /**
     * 捕捉校验异常(BindException)
     */
    @ExceptionHandler(BindException.class)
    public ResultEntity validException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> error = this.getValidError(fieldErrors);
        return ResultUtil.error(400, error.get("errorMsg").toString(), error.get("errorList"));
    }


    /**
     * 捕捉404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResultEntity handle(NoHandlerFoundException e) {
        return ResultUtil.error(404, e.getMessage());
    }


    @ExceptionHandler(BusinessException.class)
    public ResultEntity handle(BusinessException e) {
        return ResultUtil.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕捉其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public ResultEntity globalException(HttpServletRequest request, Throwable ex) {
        return ResultUtil.error(getStatus(request).value(), ex.toString() + ": " + ex.getMessage());
    }


    /**
     * 自定义抛出异常
     *
     * @param response response
     * @param msg      msg
     */
    public static void responseError(ServletResponse response, String msg) {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(403);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        try {
            String rj = new ObjectMapper().writeValueAsString(ResultUtil.error(403, msg));
            httpResponse.getWriter().append(rj);
            httpResponse.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取状态码
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * 获取校验错误信息
     */
    private Map<String, Object> getValidError(List<FieldError> fieldErrors) {
        Map<String, Object> map = new HashMap<>(16);
        List<String> errorList = new ArrayList<>();
        StringBuffer errorMsg = new StringBuffer("校验异常(ValidException):");
        for (FieldError error : fieldErrors) {
            errorList.add(error.getField() + "-" + error.getDefaultMessage());
            errorMsg.append(error.getField()).append("-").append(error.getDefaultMessage()).append(".");
        }
        map.put("errorList", errorList);
        map.put("errorMsg", errorMsg);
        return map;
    }
}
