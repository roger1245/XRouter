### 简介：

该项目主要是模仿[ARouter](https://github.com/alibaba/ARouter)和[EasyRouter](https://github.com/Xiasm/EasyRouter)写的一个路由框架。项目中有使用到ARouter路由，但是自己一直没能搞清楚其实现原理。打算趁这个机会自己写一个路由试着弄清楚ARouter到底怎么实现的。

### 对自己的帮助：

1. 对组件化架构有了比较深刻的认识
2. 能够掌握基本的apt和javapoet框架的使用
3. 可以动手实现自己的依赖注入框架
4. 对框架设计，解耦有新的理解

### 项目已实现：

1. 支持直接解析标准URL进行跳转
2. 支持多模块工程使用
3. 支持通过path获取的aop服务
4. 支持通过接口发现服务

### 功能：

1. 添加依赖和配置：

   ```groovy
   android {
       defaultConfig {
           ...
           javaCompileOptions {
               annotationProcessorOptions {
                   arguments = [moduleName: project.getName()]
               }
           }
       }
   
   ```

2. 初始化SDK：

   ```kotlin
   public class XRouterApplication extends Application {
       @Override
       public void onCreate() {
           super.onCreate();
           XRouter.Companion.init(this);
       }
   }
   ```

3. 添加注解：

   ```kotlin
   @Route(path = Config.Module1Activity)
   class Module1Activity : AppCompatActivity() {
   	...
   }
   ```

4. 发起路由操作：

   ```kotlin
   //1.路由内通过path跳转另一个Activity
   XRouter.sInstance.build(Config.Module1Activity).navigation()
   ```

5. 通过aop方式获取服务

   ```kotlin
   val hello: HelloService = XRouter.sInstance.build(Config.HELLO_SERVICE).navigation() as HelloService
   hello.sayHello(this)
   
   //实现接口
   @Route(path = Config.HELLO_SERVICE)
   public class HelloService implements IProvider {
       @Override
       public void init(Context context) {
           Toast.makeText(context, "HelloService init successfully", Toast.LENGTH_SHORT).show();
       }
       public void sayHello(Context context) {
           Toast.makeText(context, "HelloService: Hello", Toast.LENGTH_SHORT).show();
       }
   }
   ```

6. 通过接口的方式发现服务:

   ```kotlin
   val userService: IUserService? = XRouter.sInstance.navigation(IUserService::class.java)
   userService?.login(this)
   
   //实现接口
   @Route(path = Config.USER_SERVICE_IMPL)
   public class UserServiceImpl implements IUserService {
       @Override
       public void init(Context context) {
           Toast.makeText(context, "UserServiceImpl init successfully", Toast.LENGTH_SHORT).show();
       }
   
       @Override
       public void login(Context context) {
           Toast.makeText(context, "UserServiceImpl login successfully", Toast.LENGTH_SHORT).show();
   
       }
   }
   
   //实现IProvider接口
   public interface IUserService extends IProvider {
       public void login(Context context);
   }
   ```

   

### apk下载：

[URL](https://github.com/roger1245/XRouter/blob/master/apk/app-debug.apk)

