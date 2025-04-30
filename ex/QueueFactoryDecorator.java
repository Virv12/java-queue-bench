package ex;

public interface QueueFactoryDecorator {
    <T> QueueFactory<T> decorate(QueueFactory<T> factory);
}
