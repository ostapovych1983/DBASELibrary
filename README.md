# DBASE_Library

DBASE library develop for working with dbf files (table)
with java. Support annotation, dbf types.

# Usage

Use DBASEFactory to obtain DBF readers and write factories
for working with dbf files. (For now realize support only for 
Dbf3 format). Use

    DBF3 dbf3 = DBASEFactory.dbf3();

for obtain dbf3 readers and writers factory with default parameters. 
To get a dbf3 factory with supported parameters use DBFOptions class
    
    DBFOptions dbfOpetions = DBFOptions.defaultOptions();

and customisation options.

    DBFOptions dbfOpetions = DBFOptions.defaultOptions();
    options.setCodePage(DBFCodePage.RUSSIAN_WINDOWS);
    DBF3 dbf3 = DBASEFactory.dbf3();



    


    
