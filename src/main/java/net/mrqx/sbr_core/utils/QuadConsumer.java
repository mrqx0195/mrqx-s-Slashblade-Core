package net.mrqx.sbr_core.utils;

/**
 * 四参数消费型函数接口。
 * <p>
 * 接受四个参数且不返回结果，类似于 {@link java.util.function.Consumer} 的四个参数版本。
 */
@FunctionalInterface
@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
public interface QuadConsumer<T, U, V, W> {
    /**
     * 对给定的四个参数执行此操作。
     */
    void accept(T var1, U var2, V var3, W var4);
    
    /**
     * 返回一个组合的 QuadConsumer，按顺序执行当前操作后再执行 after 操作。
     */
    default QuadConsumer<T, U, V, W> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super W> after) {
        return (t, u, v, w) -> {
            this.accept(t, u, v, w);
            after.accept(t, u, v, w);
        };
    }
}
