
//打印java堆栈
console.log(Java.use("android.util.Log").getStackTraceString(Java.use("java.lang.Exception").$new()));

//列出加载的类
Java.enumerateLoadedClasses(
  {
    "onMatch": function (className) {
      console.log(className)
    },
    "onComplete": function () { }
  }
)

//初始化日志打印
setImmediate(function () {
  console.log("[*] Starting script");
});

//监控
Java.perform(function () {
  try {

  } catch (error) {
      console.log('Java.perform error:' + error);
  }
});













