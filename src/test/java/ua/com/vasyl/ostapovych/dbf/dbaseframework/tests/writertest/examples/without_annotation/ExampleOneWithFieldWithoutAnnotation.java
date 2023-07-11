package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.without_annotation;

@SuppressWarnings("unused")
public class ExampleOneWithFieldWithoutAnnotation<T> {

    private T t_field;

    public ExampleOneWithFieldWithoutAnnotation(T t_field) {
        this.t_field = t_field;
    }
    public ExampleOneWithFieldWithoutAnnotation(){}

    public T getT_field() {
        return t_field;
    }

    public void setT_field(T t_field) {
        this.t_field = t_field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleOneWithFieldWithoutAnnotation<?> that = (ExampleOneWithFieldWithoutAnnotation<?>) o;

        return t_field != null ? t_field.equals(that.t_field) : that.t_field == null;
    }

    @Override
    public int hashCode() {
        return t_field != null ? t_field.hashCode() : 0;
    }
}
