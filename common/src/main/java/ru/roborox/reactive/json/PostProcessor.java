package ru.roborox.reactive.json;

public interface PostProcessor<T> {
    void postprocess(T bean);
}
