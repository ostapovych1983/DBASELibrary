package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.validators;

import org.junit.Test;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.validation.DBFDuplicateAnnotationFieldName;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.validators.Validators;

public class ValidatorsTest {

    @Test(expected = DBFDuplicateAnnotationFieldName.class)
    public void testDuplicateAnnotatedFieldName(){
        Validators.validateMetaDataDBF(DuplicateExample.class);
    }

    private static class DuplicateExample{
        @DBFColumn(name = "Name", type = DBFType.CHARACTER)
        private String fieldNameOne;
        @DBFColumn(name = "Name", type = DBFType.CHARACTER)
        private String fieldNameTwo;

        public String getFieldNameOne() {
            return fieldNameOne;
        }

        public void setFieldNameOne(String fieldNameOne) {
            this.fieldNameOne = fieldNameOne;
        }

        public String getFieldNameTwo() {
            return fieldNameTwo;
        }

        public void setFieldNameTwo(String fieldNameTwo) {
            this.fieldNameTwo = fieldNameTwo;
        }
    }
}
