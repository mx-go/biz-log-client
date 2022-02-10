# SpringBoot-注解-日志组件

此组件解决的问题是： 「谁」在「什么时间」对「什么」做了「什么事」

1. 针对业务关键调用存储日志，便于问题追溯、定位与排查；
2. 记录用户对数据的操作，分析用户请求；

> 本组件目前针对SpringBoot做了Autoconfig，如果是SpringMVC，则需要在XML初始Bean

## 使用方式

### 基本使用

#### maven添加依赖

```xml
<dependency>
    <groupId>com.github.mx-go</groupId>
    <artifactId>biz-log-client</artifactId>
    <version>${latest-version}</version>
</dependency>
```

#### 添加 @EnableLog 注解

```java
@SpringBootApplication
@EnableLog
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
```

## 注解字段释义

| 名称         | 描述                  | 必填 | 默认值         | 举例                                                                                   |
|------------|---------------------| ---- |-------------|--------------------------------------------------------------------------------------|
| title      | 功能名称                | 是   |             | 创建订单                                                                                 |
| bizId      | 业务主键                | 否   | 空           | ZF2021071311999802                                                                   |
| logType    | 日志类型                | 否   | WEB         | LogType.WEB                                                                          |
| success    | 调用成功时取值             | 否   | 方法返回值       |                                                                                      |
| fail       | 调用失败时取值             | 否   | 调用异常详情      |                                                                                      |
| detail     | 详情                  | 否   | 方法入参        |                                                                                      |
| operatorId | 操作人                 | 否   | system      | rainbowhorse                                                                           |
| category   | 分类                  | 否   | 空           |                                                                                      |
| content    | 自定义信息(建议存放json便于解析) | 否   | 请求的信息(json) | {\"os\":\"Window10",\"ip\":\"100.119.30.17\",\"browser\":\"PostmanRuntime 7.28.3\"}" |
| conditon   | 是否存储                | 否   | true        |                                                                                      |
## 日志埋点

### 1. 普通的记录日志

- bizId：业务ID，比如订单ID，查询的时候可以根据bizId查询和它相关的操作日志
- success：方法调用成功后把success记录在日志的内容中
- SpEL 表达式：其中用双大括号包围起来的（例如：`{{#order.purchaseName}}）``#order.purchaseName` 是 SpEL表达式。Spring中支持的它都支持。比如调用静态方法，三目表达式。SpEL 可以使用方法中的任何参数。

```java
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}", success = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,下单结果:{{#_ret}}")
public boolean createOrder(Order order){
        log.info("【创建订单】orderId={}",order.getOrderId());
        // db insert order
        return true;
}
```

此时会打印操作日志 "张三下了一个订单,购买商品超值优惠红烧肉套餐」,下单结果:true"

### 2. 期望记录失败的日志, 如果抛出异常则记录fail的日志，没有则抛出记录 `success` 的日志

```java
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}", fail = "创建订单失败，失败原因：「{{#_errorMsg}}」", success = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,下单结果:{{#_ret}}")
public boolean createOrder(Order order){
        log.info("【创建订单】orderId={}",order.getOrderId());
        // db insert order
        return true;
}
```

其中的 `#_errorMsg` 是取的方法抛出异常后的异常的 `errorMessage`。

### 3. 日志支持种类

比如一个订单的操作日志，有些操作日志是用户自己操作的，有些操作是系统运营人员做了修改产生的操作日志，系统不希望把运营的操作日志暴露给用户看到，但是运营期望可以看到用户的日志以及运营自己操作的日志，这些操作日志的bizId都是订单号，所以为了扩展添加了类型字段，主要是为了对日志做分类，查询方便，支持更多的业务。

```java
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}", fail = "创建订单失败，失败原因：「{{#_errorMsg}}」", category = "MANAGER", success = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,下单结果:{{#_ret}}")
public boolean createOrder(Order order){
        log.info("【创建订单】orderId={}",order.getOrderId());
        // db insert order
        return true;
}
```

### 4. 支持记录操作的详情或者额外信息

如果一个操作修改了很多字段，但是success的日志模版里面防止过长不能把修改详情全部展示出来，这时候需要把修改的详情保存到`detail`字段， `detail`是一个 String ，需要自己序列化。这里的 `#order.toString()` 是调用了 Order 的 toString()
方法。 如果保存JSON，重写一下Order的 toString() 方法即可。

```java
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}", fail = "创建订单失败，失败原因：「{{#_errorMsg}}」", category = "MANAGER_VIEW", detail = "{{#order.toString()}}", success = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,下单结果:{{#_ret}}")
public boolean createOrder(Order order){
        log.info("【创建订单】orderId={}",order.getOrderId());
        // db insert order
        return true;
}
```

### 5. 如何指定操作日志的操作人是什么？ 框架提供了两种方法

- 第一种：手工在`@Log`的注解上指定。这种需要方法参数上有`operatorId`

```java
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}", fail = "创建订单失败，失败原因：「{{#_errorMsg}}」", category = "MANAGER_VIEW", content = "{{#order.toString()}}", operatorId = "{{#currentUser}}", success = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,下单结果:{{#_ret}}")
public boolean createOrder(Order order,String currentUser){
  log.info("【创建订单】orderId={}",order.getOrderId());
  // db insert order
  return true;
}
```

这种方法手工指定，需要方法参数上有 `operatorId` 参数，或者通过 SpEL 调用静态方法获取当前用户。

- 第二种： 通过默认实现类来自动的获取操作人，由于在大部分web应用中当前的用户都是保存在一个线程上下文中的，所以每个注解都加一个`operatorId`获取操作人显得有些重复劳动，所以提供了一个扩展接口来获取操作人 框架提供了一个扩展接口，使用框架的业务可以 `implements`
  这个接口自己实现获取当前用户的逻辑， 对于使用 `SpringBoot` 的只需要实现 `LogOperator` 接口，然后把这个 `Service` 作为一个单例放到 `Spring` 的上下文中。使用 `Spring MVC` 的就需要自己手工装配这些Bean。

```java
@Configuration
public class LogConfiguration {

    @Bean
    public LogOperator operatorGetService() {
        return () -> OrgUserUtils.getCurrentUserId();
    }
}

// 也可以这么做
@Service
public class DefaultLogOperatorImpl implements LogOperator {

    @Override
    public String getOperatorId() {
        return OrgUserUtils.getCurrentUserId();
    }
}
```

### 6. 自定义函数

对于更新等方法，方法的参数上大部分都是订单ID、或者产品ID等， 比如：日志记录的`success`内容是："更新了订单`{{#orderId}}`，更新内容为...."，这种对于运营或者产品来说难以理解，所以引入了自定义函数的功能。 使用方法是在原来的变量的两个大括号之间加一个函数名称 例如 "`{ORDER{#orderId}}`" 其中 `ORDER` 是一个函数名称。只有一个函数名称是不够的,需要添加这个函数的定义和实现。可以看下面例子 自定义的函数需要实现框架里面的`ParseFunction`的接口，需要实现两个方法：

- `functionName()`方法就返回注解上面的函数名；
- `apply()`函数参数是 "`{ORDER{#orderId}}`"中SpEL解析的`#orderId`的值，这里是一个数字1223110，接下来只需要在实现的类中把 ID 转换为可读懂的字符串就可以了， 一般为了方便排查问题需要把名称和ID都展示出来，例如："订单名称（ID）"的形式。

> 对于SpringBoot应用很简单，只需要把它暴露在Spring的上下文中就可以了，可以加上Spring的 @Component 或者 @Service 很方便😄。SpringMVC应用需要自己装配 Bean。

```java
// 没有使用自定义函数
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}", success = "更新了订单{{#orderId}},更新内容为....", detail = "{{#order.toString()}}")
public boolean update(Long orderId,Order order){
        return false;
}

//使用了自定义函数，主要是在 {{#orderId}} 的大括号中间加了 functionName
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}", success = "更新了订单{ORDER{#orderId}},更新内容为....", detail = "{{#order.toString()}}")
public boolean update(Long orderId,Order order){
        return false;
}

// 还需要加上函数的实现
@Component
public class OrderParseFunction implements ParseFunction {
    @Resource
    @Lazy // 为了避免类加载顺序的问题 最好为Lazy，没有问题也可以不加
    private OrderQueryService orderQueryService;

    @Override
    public String functionName() {
        // 函数名称为 ORDER
        return "ORDER";
    }

    @Override
    //这里的value可以把Order的JSON对象的传递过来，然后反解析拼接一个定制的操作日志内容
    public String apply(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        Order order = orderQueryService.queryOrder(Long.parseLong(value));
        //把订单产品名称加上便于理解，加上ID便于查问题
        return order.getProductName().concat("(").concat(value).concat(")");
    }
}
```

### 7. SpEL 三目表达式

```java
@Log(logType = LogType.WEB, bizId = "{{#businessLineId}}", success = "{{#disable ? '停用' : '启用'}}了自定义属性{ATTRIBUTE{#attributeId}}")
public CustomAttributeVO disableAttribute(Long businessLineId, Long attributeId, boolean disable){
    return xxx;
}
```

### 8. 模版中使用方法参数之外的变量&函数中使用Context中变量

可以在方法中通过 `LogContext.putVariable(variableName, Object)` 的方法添加变量，第一个对象为变量名称，后面为变量的对象， 然后就可以使用 SpEL 使用这个变量了，例如：例子中的 `{{#innerOrder.productName}}` 是在方法中设置的变量，除此之外，在上面提到的自定义函数中也可以使用`LogContext`中的变量。(注意：`LogContext`中变量的生命周期为这个方法，超出这个方法，方法中set到Context的变量就获取不到了）

```java
@Log(logType = LogType.WEB, bizId = "{{#order.orderId}}",
     success = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,测试变量「{{#innerOrder.productName}}」,下单结果:{{#_ret}}",)
public boolean createOrder(Order order){
  log.info("【创建订单】orderId={}",order.getOrderId());
  // db insert order
  Order order = new Order();
  order.setProductName("内部变量测试");
  LogContext.putVariable("innerOrder", order);
  return true;
}
```

### 9. 函数中使用LogContext的变量

使用 `LogContext.putVariable(variableName, Object)` 添加的变量除了可以在注解的 SpEL 表达式上使用，还可以在自定义函数中使用 这种方式比较复杂，下面例子中示意了列表的变化，比如 从[A,B,C] 改到 [B,D] 那么日志显示：「删除了A，增加了D」

```java
@Log(logType = LogType.WEB, success = "{DIFF_LIST{'文档地址'}}", bizId = "{{#id}}")
public void updateRequirementDocLink(String currentMisId,Long id,List<String> docLinks){
        RequirementDO requirementDO=getRequirementDOById(id);
        LogContext.putVariable("oldList",requirementDO.getDocLinks());
        LogContext.putVariable("newList",docLinks);

        requirementModule.updateById("docLinks",RequirementUpdateDO.builder()
        .id(id)
        .docLinks(docLinks)
        .updater(currentMisId)
        .updateTime(new Date())
        .build());
}


@Component
public class DiffListParseFunction implements ParseFunction {

    @Override
    public String functionName() {
        return "DIFF_LIST";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String apply(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        List<String> oldList = (List<String>) LogContext.getVariable("oldList");
        List<String> newList = (List<String>) LogContext.getVariable("newList");
        oldList = oldList == null ? Lists.newArrayList() : oldList;
        newList = newList == null ? Lists.newArrayList() : newList;
        Set<String> deletedSets = Sets.difference(Sets.newHashSet(oldList), Sets.newHashSet(newList));
        Set<String> addSets = Sets.difference(Sets.newHashSet(newList), Sets.newHashSet(oldList));
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(addSets)) {
            stringBuilder.append("新增了 <b>").append(value).append("</b>：");
            for (String item : addSets) {
                stringBuilder.append(item).append("，");
            }
        }
        if (CollectionUtils.isNotEmpty(deletedSets)) {
            stringBuilder.append("删除了 <b>").append(value).append("</b>：");
            for (String item : deletedSets) {
                stringBuilder.append(item).append("，");
            }
        }
      return StringUtils.isBlank(stringBuilder) ? null : stringBuilder.substring(0, stringBuilder.length() - 1);
}
```

## 扩展点

- 重写`LogOperator`通过上下文获取用户的扩展，例子如下

```java
@Service
public class DefaultLogOperator implements LogOperator {

    @Override
    public String getOperatorId() {
         return UserUtils.getUserId();
    }
}
```

- `LogPersistence`保存/查询日志的例子,使用者可以根据数据量保存到合适的存储介质上，比如保存在数据库/或者ES。自己实现保存和删除就可以了

> 也可以只实现保存的接口，毕竟已经保存在业务的存储上了，查询业务可以自己实现，不走 LogPersistence 这个接口，毕竟产品经理会提一些千奇百怪的查询需求。

```java
@Service
public class DbLogServiceImpl implements LogPersistence {

    @Resource
    private LogMapper logMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(LogRecord logRecord) {
        log.info("【logRecord】log={}", logRecord);
        LogRecordPO logRecordPO = LogRecordPO.toPo(logRecord);
        logRecordMapper.insert(logRecordPO);
    }
}
```

-  `ParseFunction`定义转换函数的接口，可以实现`ParseFunction` 实现对`@Log`注解中使用的函数扩展，例子：

```java
@Component
public class UserParseFunction implements ParseFunction {
  
    private final Splitter splitter = Splitter.on(",").trimResults();

    @Resource
    @Lazy
    private UserQueryService userQueryService;

    @Override
    public String functionName() {
        return "USER";
    }

    @Override
    // 11,12 返回 11(小明，张三)
    public String apply(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        List<String> userIds = Lists.newArrayList(splitter.split(value));
        List<User> misDOList = userQueryService.getUserList(userIds);
        Map<String, User> userMap = StreamUtils.extractMap(misDOList, User::getId);
        StringBuilder stringBuilder = new StringBuilder();
        for (String userId : userIds) {
            stringBuilder.append(userId);
            if (userMap.get(userId) != null) {
                stringBuilder.append("(").append(userMap.get(userId).getUsername()).append(")");
            }
            stringBuilder.append(",");
        }
        return stringBuilder.toString().replaceAll(",$", "");
    }
}
```

## 变量相关

`@Log`可以使用的变量出了参数也可以使用参数`#_params`，返回值`#_ret`变量，以及异常的错误信息`#_errorMsg`，也可以通过`SpEL的T方式`调用静态方法。