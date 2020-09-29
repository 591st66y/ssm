## SSM-Token

> ssm搭建前后端分离服务端系统,基于无状态token认证.

#### 1. 加入JWT依赖

```xml

    <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>${jwt-version}</version>
     </dependency>
```


#### 2. JwtInterceptor

```java
/**
 * token 拦截器
 *
 * @Author zhangyukang
 * @Date 2020/8/5 18:16
 * @Version 1.0
 **/
public class JwtInterceptor implements HandlerInterceptor {


    private Logger logger= LoggerFactory.getLogger(JwtInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("url:" + request.getRequestURL() + "被拦截");

        /** token 验证 */
        String token = request.getHeader(JwtUtil.getHeader());
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter(JwtUtil.getHeader());
        }
        if (StringUtils.isEmpty(token)) {
            throw new SignatureException(JwtUtil.getHeader() + "不能为空");
        }
        Claims claims;
        try {
            claims = JwtUtil.getTokenClaim(token);
            if (claims == null || JwtUtil.isTokenExpired(claims.getExpiration())) {
                throw new SignatureException(JwtUtil.getHeader() + "失效，请重新登录。");
            }
        } catch (Exception e) {
            throw new SignatureException(JwtUtil.getHeader() + "失效，请重新登录。");
        }

        /** 设置 identityId 用户身份ID,用户信息等. */
        User user = new User();
        user.setId(Integer.valueOf(claims.getSubject()));
        user.setUsername((String) claims.get("username"));
        user.setRole((String) claims.get("role"));
        WebUtil.setUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WebUtil.remove();
    }
}

```

#### 3. 配置拦截器

```xml
    <!-- 配置拦截器 -->
    <mvc:interceptors>
        <mvc:interceptor>
            <mvc:mapping path="/**"/>
            <mvc:exclude-mapping path="/user/login"/>
            <bean class="com.coderman.common.JwtInterceptor"/>
        </mvc:interceptor>
    </mvc:interceptors>
```

#### 4. 登入方法

```java

/**
 * @Author zhangyukang
 * @Date 2020/8/11 09:24
 * @Version 1.0
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private Logger logger=LoggerFactory.getLogger(this.getClass());

    /**
     * 用户登入
     * @param user
     * @return
     * @throws UserException
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public JsonData login(@RequestBody User user) throws UserException {
        BeanValidator.check(user);
        User loginUser=userService.login(user.getUsername(),user.getPassword());
        //user对象转map
        BeanMap map = BeanMap.create(user);
        String token = JwtUtil.createToken(loginUser.getId(), map);
        logger.info("用户:{},成功登入,返回token:{}",loginUser,token);
        return JsonData.success(token);
    }
}

```
