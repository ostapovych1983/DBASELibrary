package ua.com.vasyl.ostapovych.dbf.dbaseframework.impl.utils;

import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.enums.DBFCodePage;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFFileReadException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalFieldException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.exceptions.DBFIllegalValueException;
import ua.com.vasyl.ostapovych.dbf.dbaseframework.api.dbf.fields.DBFField;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static java.lang.System.arraycopy;

class __ByteArrayUtils__ {
    private static final String DBF_DATE_FORMAT = "yyyyMMdd";
    private final __DBFUtils__ dbfUtils = new __DBFUtils__();
    private final __AccessorsUtils__ accessorsUtils = new __AccessorsUtils__();

    byte[] readBytes(DataInput dataInput, int size) {
        if (dataInput == null) throw new IllegalArgumentException("Parameter dataInput cannot be a null");
        byte[] res = new byte[size];
        try {
            dataInput.readFully(res);
        } catch (IOException e) {
            throw new DBFFileReadException(e);
        }
        return res;
    }

    byte[] readBytes(byte[] mass, int startPosition, int count) {
        return Arrays.copyOfRange(mass,startPosition,startPosition+count);
    }

    int readBytesAsInt(byte[] bytes) {
        if (bytes.length<4){
            byte [] filledZeroArrays = new byte[4];
            arraycopy(bytes, 0, filledZeroArrays, 0, bytes.length);
            return ByteBuffer.wrap(filledZeroArrays).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }if (bytes.length>4){
            byte [] fixedMas = new byte[4];
            arraycopy(bytes, 0, fixedMas, 0, 4);
            return ByteBuffer.wrap(fixedMas).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    char readAsChar(byte b) {
        return (char) b;
    }

    byte[] trimBytes(byte[] readBytes) {
        int size = 0;
        for (byte b:readBytes) if (b!=0x00) size++;
        byte [] res = new byte[size];
        int index = 0;
        for (byte b:readBytes) if (b!=0x00) res[index++] = b;
        return res;
    }

    byte[] dateToByteArray(Date date) {
        byte[] res = new byte[8];
        if (date == null) return new byte[0];
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DBF_DATE_FORMAT);
        String dateAsString = simpleDateFormat.format(date);
        byte[] dateAsByteArray = dateAsString.getBytes();
        arraycopy(dateAsByteArray, 0, res, 0, dateAsString.getBytes().length);
        return res;
    }

    byte[] floatToByteArray(Float asFloat, DBFField field) {
        return stringToByteArray(String.valueOf(asFloat),field);
    }

    byte[] booleanToByteArray(Boolean asLogic) {
        byte[] res = new byte[1];
        res[0] = (byte) (asLogic ? 84:70);
        return res;
    }

    byte[] integerToByteArray(Integer asInteger, DBFField dbfField) {
        return stringToByteArray(String.valueOf(asInteger),dbfField);
    }

    byte[] doubleToByteArray(Double asDouble, DBFField dbfField) {
        return stringToByteArray(String.valueOf(asDouble),dbfField);
    }

    <T> byte[] toDBFRecord(DBFField[] fields, T row) {
        byte[] res = new byte[dbfUtils.calculateRecordSize(fields)];
        res[0] = 0x20;
        int currentPositionRes = 1;
        for (DBFField field : fields) {
            byte[] fieldAsByteArray = toDBFFieldAsByteArray(field, row);
            arraycopy(fieldAsByteArray, 0, res, currentPositionRes, fieldAsByteArray.length);
            currentPositionRes+=field.getSize();
        }
        return res;
    }


    <T> byte[] toDBFFieldAsByteArray(DBFField dbfField, T row) {
        Object value = accessorsUtils.getValueFromObject(dbfField,row);
        return _getValueFromObject(dbfField,value);
    }

    private byte[] _getValueFromObject(DBFField dbfField, Object value) {
        switch (dbfField.getDbfType()) {
            case DATE:
                return dateToByteArray((Date) value);
            case CHARACTER:
                return stringToByteArray((String)value,dbfField);
            case FLOAT: {
                if (value instanceof Double)
                    return floatToByteArray(((Double) value).floatValue(), dbfField);
                if (value instanceof Integer){
                    return floatToByteArray(((Integer) value).floatValue(), dbfField);
                }else return floatToByteArray((Float) value, dbfField);
            }
            case LOGICAL:
                return booleanToByteArray((Boolean) value);
            case NUMERIC: {
                if (value instanceof Double) return doubleToByteArray((Double) value,dbfField);
                else return (integerToByteArray((Integer) value,dbfField));
            }
        }
        return null;
    }

    byte[] stringToByteArray(String value, DBFField f) {
        if (value == null || value.isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<f.getSize();i++){

                stringBuilder.append(0x00);
            }
            return stringBuilder.toString().getBytes();
        }
        if (value.length() > f.getSize()){
            throw new DBFIllegalValueException(String.format("Cannot set string value %s to field %s. Size value = %s. Size field = %s",value,f.getName(),value.length(),f.getSize()));
        }
        if (value.length() == f.getSize()) return value.getBytes();
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        for (int i=value.length();i<f.getSize();i++){
            sb.append(' ');
        }
        return sb.toString().getBytes();
    }

    byte[] createCaption(DBFField[] fields, int size) {
        short captionSize = (short) (32+(32*fields.length)+1);
        byte [] caption = new byte[captionSize];
        caption[0] = 0x3;
        byte monthLastUpdate = _getFromDate(Calendar.MONTH);
        byte dayOfLastUpdate = _getFromDate(Calendar.DAY_OF_MONTH);
        byte yearLastUpdate = _getFromDate(Calendar.YEAR);
        caption[1] = monthLastUpdate;
        caption[2] = dayOfLastUpdate;
        caption[3] = yearLastUpdate;
        byte[] recordCount = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(size).array();
        arraycopy(recordCount, 0, caption, 4, recordCount.length);
        byte[] headerSize = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(captionSize).array();
        arraycopy(headerSize,0,caption,8,headerSize.length);
        short recordSize = this.dbfUtils.calculateRecordSize(fields);
        byte[] recordSizeAsByteArray = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(recordSize).array();
        arraycopy(recordSizeAsByteArray,0,caption,10,recordSizeAsByteArray.length);
        caption[29] = 0x65;
        byte[] fieldDefinitionAsByteArray = getFieldDefinition(fields);
        arraycopy(fieldDefinitionAsByteArray,0,caption,32,fieldDefinitionAsByteArray.length);
        return caption;
    }

    byte _getFromDate(int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (dayOfMonth == Calendar.YEAR){
            String yearAsString = String.valueOf(calendar.get(Calendar.YEAR));
            return Byte.parseByte(yearAsString.substring(2));
        }
        int m = calendar.get(dayOfMonth)+1;
        return (byte) m;
    }

    byte[] getFieldDefinition(DBFField[] fields) {
        byte[] res = new byte[32*fields.length];
        int startPositionToCopy = 0;
        for (DBFField field:fields){
            byte [] fieldDefinitionAsByteArray = getFieldDefinitionAsByteArray(field);
            arraycopy(fieldDefinitionAsByteArray,0,res,startPositionToCopy,fieldDefinitionAsByteArray.length);
            startPositionToCopy += 32;
        }
        return res;
    }

    byte[] getFieldDefinitionAsByteArray(DBFField field) {
        byte[] res = new byte[32];
        byte[] fieldNameByBytes = new byte[11];
        String fieldName = field.getName();
        if (fieldName.length()<=11){
            arraycopy(fieldName.getBytes(),0,fieldNameByBytes,0,fieldName.getBytes().length);
        }else{
            throw new DBFIllegalFieldException("Size of dbf field's name cannot be more 11 ");
        }
        arraycopy(fieldNameByBytes,0,res,0,fieldNameByBytes.length);
        res[11] = (byte) field.getDbfType().getSymbol();
        res[16] = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) field.getSize()).get(0);
        res[17] = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) field.getDecimalSize()).get(0);
        res[18] = 0x02;
        return res;

    }

    void skipBytes(DataInput dataInput) {
        try {
            dataInput.skipBytes(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] toDBFRecord(DBFField[] fields,Object[] row) {
        if (fields.length > row.length) throw new IllegalArgumentException("Count of row cannot be less than count of dbfFields");
        byte[] res = new byte[dbfUtils.calculateRecordSize(fields)];
        res[0] = 0x20;
        int currentPositionRes = 1;
        for (int i=0;i< fields.length;i++){
            DBFField field = fields[i];
            byte[] fieldAsByteArray = toDBFFieldAsByteArray(field, row, i);
            arraycopy(fieldAsByteArray, 0, res, currentPositionRes, fieldAsByteArray.length);
            currentPositionRes+=field.getSize();
        }
        return res;
    }

    private byte[] toDBFFieldAsByteArray(DBFField dbfField, Object[] row, int i) {
        Object value = row[i];
        return _getValueFromObject(dbfField,value);
    }

    public Object readValueFromBytes(byte[] rawValue, DBFField field, DBFCodePage dbfCodePage) {
        switch (field.getDbfType()){
            case CHARACTER:
            case UNKNOWN:
                return new String (rawValue,dbfCodePage.getJavaCharSet()).trim();
            case DATE: return getDateFromBytes(rawValue);
            case FLOAT: return getFloatFromByteArray(rawValue);
            case NUMERIC: return getNumericFromByteArray(rawValue,field);
            case MEMO: return null;
            case LOGICAL: return getLogicalFromByteArray(rawValue);
        }
        return null;
    }

    private Boolean  getLogicalFromByteArray(byte[] rawValue) {
        byte b = rawValue[0];
        if (b == 84) return true;
        if (b == 70) return false;
        if (b == 0) return false;
        throw new DBFIllegalValueException("Cannot set value "+rawValue[0]+ " to Boolean. " +
                "Value Should be "+(byte)84 +" or "+ (byte)70);
    }

    private Number getNumericFromByteArray(byte[] rawValue, DBFField field) {
        int decimalSize = field.getDecimalSize();
        String valAsString = new String(rawValue).trim();
        if (valAsString.isEmpty()) return 0;
        if (decimalSize ==0) return Integer.parseInt(valAsString);
        return Double.parseDouble(valAsString);
    }

    private Float getFloatFromByteArray(byte[] rawValue) {
        String valAsString = new String(rawValue).trim();
        if (valAsString.isEmpty()) return 0.0f;
        return Float.parseFloat(valAsString);
    }

    private Date getDateFromBytes(byte[] rawValue) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            return simpleDateFormat.parse(new String(rawValue));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
