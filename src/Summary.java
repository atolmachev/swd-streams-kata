import java.util.Collections;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class Summary<T> {

    public final T min;
    public final T max;
    public final int count;

    private Summary(T min, T max, int count) {
        this.min = min;
        this.max = max;
        this.count = count;
    }

    private static class SummaryBuilder<T> {
        public T min;
        public T max;
        public int count;

        public SummaryBuilder(T min, T max, int count) {
            this.min = min;
            this.max = max;
            this.count = count;
        }

        public SummaryBuilder() {

        }


        public Summary<T> get() {
            return new Summary<>(min, max, count);
        }
    }

    public static <T extends Comparable<T>> Summary<T> ofStream(Stream<? extends T> stream) {

        return stream.collect(new Collector<T, SummaryBuilder<T>, Summary<T>>() {
            @Override
            public Supplier<SummaryBuilder<T>> supplier() {
                return SummaryBuilder::new;
            }

            @Override
            public BiConsumer<SummaryBuilder<T>, T> accumulator() {
                return (summary, t) -> {
                    summary.min = summary.min == null ? t : min(summary.min, t);
                    summary.max = summary.max == null ? t : max(summary.max, t);
                    summary.count++;
                };
            }

            @Override
            public BinaryOperator<SummaryBuilder<T>> combiner() {
                return (tSummaryBuilder, tSummaryBuilder2) -> {
                    T min1 = min(tSummaryBuilder2.min, tSummaryBuilder.min);
                    T max1 = max(tSummaryBuilder2.max, tSummaryBuilder.max);
                    return new SummaryBuilder<T>(min1, max1, tSummaryBuilder.count + tSummaryBuilder2.count);
                };
            }

            @Override
            public Function<SummaryBuilder<T>, Summary<T>> finisher() {
                return SummaryBuilder::get;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        });
    }

    private static <T extends Comparable<T>> T max(T value1, T value2) {
        return value2.compareTo(value1) <= 0 ? value1 : value2;
    }


    private static <T extends Comparable<T>> T min(T value1, T value2) {
        return value2.compareTo(value1) >= 0 ? value1 : value2;
    }
}
