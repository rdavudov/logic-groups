# linked-logics
Chain of Responsibility Design pattern is one essential methods to develop extensible applications.
> Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it. -- Gang of Four


Linked-Logics is a Spring based library which provides an implementation of [chain of responsibility design pattern](https://en.wikipedia.org/wiki/Chain-of-responsibility_pattern). Here there are several entities which help us to configure and run these chains. 

*Why Spring-based ?* Spring framework provides powerfull IoC and Configuration capabilities which is required features in most frameworks. And since it is very popular, we have decided to use its power for our dependency injection and configuration management. 

### Logics
Logics are atomic operations (actually methods) for doing specific work. Logics can be reused across execution which applies DRY principles. Logics may accept inputs and parameters and return values to be used by other logics. Also logics can alter execution flow of chain by throwing exceptions in error cases. 

```java

    @Logic("add")
    public void add(@ContextParam(value = "list") List<String> list, @InputParam(value = "item") String item) {
        list.add(item) ;
    }

    @Logic(value = "remove", returnAs = "removed")
    public boolean remove(@ContextParam("list") List<String> list, @InputParam("item") String item) {
       return list.remove(item) ;
    } 
```

### Groups
Groups are container for logics by applying extra configurability for logic and logics execution.

```java

    @Group("group")
    public LogicGroup activation(LogicGroupBuilder builder) throws IOException {
        return builder
                .group("group1")
                  .logic("add1", "add").input("item", "item1").undo("remove")
                  .logic("add2", "add").input("item", "item2").undo("remove")
                .finish()
                .group("group2")
                  .logic("add3", "add").input("item", "item3").undo("remove")
                  .logic("add4", "add").input("item", "item4").undo("remove")
                .finish()
                .build() ;
     }
```

### Context
Context is an execution start point. Context is created for each request and contains all initial parameters as well as modified or appended ones. Context is responsible for correct execution of chain according to Group and Logic definitions. At the end it returns a Result object containing result of execution and additional parameters exported from execution.

```java
    @Autowired
    private LogicContextManager contextManager ;
    
    public Result execute() {
        ExecutableContext context = contextManager.newContext() ;
        context.setEntry("group") ;
        return context.execute() ;
    }
```

