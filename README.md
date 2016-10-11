# RainbowTBRetrofit
#### Top：基于square 出的 Retrofit 底层框架封装的 http请求库

**说明:**
    <p> 1. 对各Factory进行初始化封装，更换日志打印拦截器，提供debug模式 </p>
    <p> 2. 对Retrofit 接口定义进行了常用模式归纳，具体请求模式限定和抽象 </p>
    <p> 3. 对请求参数统一格式化(GET , JSON,两种 FormData),保持入口参数设置方式相同，方便使用 </p>
    <p> 4. 对CallBack返回统一使用String泛型，替换GsonConveter 为StringConverter </p>
    <p> 5. 除了CallBack<String> 固定其他Factory类都可以解耦单独立使用 </p>
  
  

   