package ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations;


import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFColumn;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.annotations.DBFTable;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.types.DBFType;

import java.util.Objects;

import static ua.com.vasyl.ostapovych.dbf.dbaseframework.tests.writertest.examples.with_annotations.ExampleWithOneStringColumn.CHARACTER_EXAMPLE_TABLE_NAME;

@DBFTable(name = CHARACTER_EXAMPLE_TABLE_NAME)
@SuppressWarnings("unused")
public class ExampleWithOneStringColumn {
    public static final String CHARACTER_EXAMPLE_TABLE_NAME = "CharacterExampleTable" ;
    @SuppressWarnings("SpellCheckingInspection")
    @DBFColumn(name = "CFIELD", type = DBFType.CHARACTER, size = 20)
    private String character_field;

    public ExampleWithOneStringColumn(String character_field) {
        this.character_field = character_field;
    }

    public ExampleWithOneStringColumn(){
    }

    public String getCharacter_field() {
        return character_field;
    }

    public void setCharacter_field(String character_field) {
        this.character_field = character_field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExampleWithOneStringColumn that = (ExampleWithOneStringColumn) o;

        return Objects.equals(character_field, that.character_field);
    }

    @Override
    public int hashCode() {
        return character_field != null ? character_field.hashCode() : 0;
    }
}
