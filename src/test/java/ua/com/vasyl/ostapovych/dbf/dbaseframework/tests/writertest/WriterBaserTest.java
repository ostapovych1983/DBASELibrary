package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.strategies.ExampleDBFTable;

import java.util.ArrayList;
import java.util.List;

import static ua.com.vasyl.ostapovych.utils.RandomUtils.*;

public class WriterBaserTest {


    private List<ExampleDBFTable> generateList(int size) {
        List<ExampleDBFTable> res = new ArrayList<>();
        for (int i=0; i<size;i++){
            res.add(generateDBFTableExample());
        }
        return res;
    }

    private ExampleDBFTable generateDBFTableExample() {
        ExampleDBFTable res = new ExampleDBFTable();
        res.setBooleanColumn(randomBoolean());
        res.setDateColumn(randomDate());
        res.setStringColumn(randomString(50,26));
        return res;
    }
}
