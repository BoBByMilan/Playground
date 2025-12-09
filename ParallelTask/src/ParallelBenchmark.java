import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParallelBenchmark {

    record Results(List<Integer> wholeNumbers, List<Double> fractionNumbers ) {}

    public Results assortmentSequential(List<Double> inList) {
        List<Integer> listOfWholes = new ArrayList<>(inList.size());
        List<Double> listOfFractions = new ArrayList<>(inList.size());
        for (Double number : inList) {
            listOfWholes.add(number.intValue());
            listOfFractions.add(number.doubleValue());
        }
        return new Results(listOfWholes, listOfFractions);
        }

        public Results assortmentParallel(List<Double> inList){
            return inList.parallelStream()
                    .collect(Collectors.teeing(
                            Collectors.mapping(Double::intValue, Collectors.toList()),
                            Collectors.mapping(Double::doubleValue, Collectors.toList()),
                            Results::new
                    ));
        }
}

