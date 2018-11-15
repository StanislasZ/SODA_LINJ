package com.cisco.fcp.entity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FieldForSimulator {

    int index; //从0开始 ，表示产生一行数据时的位置

    String name;
    String type;
    String fixed;
    String suffix;  //字符串的后缀，为数字， 自增。
    String format;
    String offset; //date的偏移量(hours)
    String date_interval;  //单位是秒

    String start;
    String step;

    String number_type;
    String range_left;
    String range_right;
    String decimal_point;


    String example_value;

    public String getExample_value() {
        return example_value;
    }

    public void setExample_value() {
        if (this.type.equals("string")){

            if(!(this.suffix.equals(""))){
                this.example_value=this.fixed+suffix;

            }else{
                this.example_value=this.fixed;
            }
        }else if (this.type.equals("number")){
            if(this.fixed.length()>0){
                this.example_value=this.fixed;
            }else if(this.range_left.length()>0){
                //不是固定值的情况, 有范围的随机数


                //System.out.println("left is "+left+", right is "+right);
                Random r=new Random();
                if(number_type.equals("integer")){
                    //有范围的整数
                    int integer_left=Integer.parseInt(range_left);
                    int integer_right=Integer.parseInt(range_right);
                    int target=r.nextInt(integer_right)%(integer_right-integer_left+1)+integer_left;
                    this.example_value=target+"";

                }else {
                    //有范围的小数，且指定了小数部分的位数
                    double decimal_left=Double.parseDouble(range_left);
                    double decimal_right=Double.parseDouble(range_right);
                    double target=r.nextDouble()*(decimal_right-decimal_left)+decimal_left;
                    int decimalPoint=Integer.parseInt(this.decimal_point);
                    String format="#.";
                    for(int i=0;i<decimalPoint;i++){
                        format=format+"0";
                    }
                    DecimalFormat dFormat=new DecimalFormat(format);
                    String targetstr=dFormat.format(target);
                    Double temp= Double.valueOf(targetstr);
                    //System.out.println(temp);
                    this.example_value=temp+"";

                }

            }else if (this.start.length()>0){
                //自增的方式
                this.example_value=this.start+"";

                try {
                    //int step_int = Integer.parseInt(this.step);
                    int start_int = Integer.parseInt(this.start);
                    int auto_result_int = start_int;

                    this.example_value = auto_result_int + "";
                }catch(Exception e){
                    //System.out.println(e);
                    //double step_double=Double.parseDouble(this.step);
                    double start_double=Double.parseDouble(this.start);
                    double auto_result_double=start_double;
                    this.example_value = auto_result_double + "";

                }finally {
                    if(decimal_point.length()>0){
                        //System.out.println("come on!!!!");
                        this.example_value=getDoubleAfterFormat(Integer.parseInt(decimal_point),Double.parseDouble(this.example_value))+"";


                    }


                }







            }



        }else if (this.type.equals("date")){
            int offset_int=0;
            if(offset.length()>0){
                offset_int=Integer.parseInt(offset);
            }
            //System.out.println("offset is "+offset_int);


            Date date=new Date();
            //System.out.println("format is "+this.format);
            SimpleDateFormat sdf=new SimpleDateFormat(this.format);
            String result=sdf.format(date.getTime()-(long)(offset_int * 60 * 60 * 1000));

            this.example_value=result;



        }
    }

    public double getDoubleAfterFormat(int decimalPoint,double target){
        String format="#.";
        for(int j=0;j<decimalPoint;j++){
            format=format+"0";
        }
        DecimalFormat dFormat=new DecimalFormat(format);
        String targetstr=dFormat.format(target);
        Double temp= Double.valueOf(targetstr);

        return temp;
    }


    public String getJson_value(int i) {
        String result="";
        if (this.type.equals("string")){

            if(!(this.suffix.equals(""))){
                int suffix_int=Integer.parseInt(suffix);
                int real_suffix=suffix_int+i;
                result=this.fixed+real_suffix;

            }else{
                result=this.fixed;
            }
        }else if (this.type.equals("number")){
            if(this.fixed.length()>0){
                result=this.fixed;
            }else if(this.range_left.length()>0){
                //不是固定值的情况, 有范围的随机数


                //System.out.println("left is "+left+", right is "+right);
                Random r=new Random();
                if(number_type.equals("integer")){
                    //有范围的整数
                    int integer_left=Integer.parseInt(range_left);
                    int integer_right=Integer.parseInt(range_right);
                    int target=r.nextInt(integer_right)%(integer_right-integer_left+1)+integer_left;
                    result=target+"";

                }else {
                    //有范围的小数，且指定了小数部分的位数
                    double decimal_left=Double.parseDouble(range_left);
                    double decimal_right=Double.parseDouble(range_right);
                    double target=r.nextDouble()*(decimal_right-decimal_left)+decimal_left;
                    int decimalPoint=Integer.parseInt(this.decimal_point);
//                    String format="#.";
//                    for(int j=0;j<decimalPoint;j++){
//                        format=format+"0";
//                    }
//                    DecimalFormat dFormat=new DecimalFormat(format);
//                    String targetstr=dFormat.format(target);
//                    Double temp= Double.valueOf(targetstr);
                    double temp=getDoubleAfterFormat(decimalPoint,target);
                    //System.out.println(temp);
                    result=temp+"";

                }

            }else if (this.start.length()>0){
                //自增的方式

                try {
                    int step_int = Integer.parseInt(this.step);
                    int start_int = Integer.parseInt(this.start);
                    int auto_result_int = start_int + step_int * i;

                    result = auto_result_int + "";
                }catch(Exception e){
                    //System.out.println(e);
                    double step_double=Double.parseDouble(this.step);
                    double start_double=Double.parseDouble(this.start);
                    double auto_result_double=start_double+step_double*i;
                    result = auto_result_double + "";

                }finally {
                    //System.out.println(decimal_point==null);   //false
                    //System.out.println(decimal_point.equals(""));   //true
                    if(decimal_point.length()>0){
                        //System.out.println("come on!!!!");
                        result=getDoubleAfterFormat(Integer.parseInt(decimal_point),Double.parseDouble(result))+"";


                    }


                }

            }



        }else if (this.type.equals("date")){     //日期类型
            int offset_int=0;
            if(offset.length()>0){
                offset_int=Integer.parseInt(offset);
            }
            //System.out.println("offset is "+offset_int);
            int date_interval_int=Integer.parseInt(this.date_interval);

            Date date=new Date();
            //System.out.println("format is "+this.format);
            SimpleDateFormat sdf=new SimpleDateFormat(this.format);
            String date_result=sdf.format(date.getTime()
                    -(long)(offset_int * 60 * 60 * 1000)
                    +(long)(date_interval_int*1000*i));

            result=date_result;



        }
        return result;
    }


    public String getDate_interval() {
        return date_interval;
    }

    public void setDate_interval(String date_interval) {
        this.date_interval = date_interval;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getNumber_type() {
        return number_type;
    }

    public void setNumber_type(String number_type) {
        this.number_type = number_type;
    }

    public String getDecimal_point() {
        return decimal_point;
    }

    public void setDecimal_point(String decimal_point) {
        this.decimal_point = decimal_point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getRange_left() {
        return range_left;
    }

    public void setRange_left(String range_left) {
        this.range_left = range_left;
    }

    public String getRange_right() {
        return range_right;
    }

    public void setRange_right(String range_right) {
        this.range_right = range_right;
    }


    @Override
    public String toString() {
        return "FieldForSimulator{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", fixed='" + fixed + '\'' +
                ", suffix='" + suffix + '\'' +
                ", format='" + format + '\'' +
                ", offset='" + offset + '\'' +
                ", date_interval='" + date_interval + '\'' +
                ", start='" + start + '\'' +
                ", step='" + step + '\'' +
                ", number_type='" + number_type + '\'' +
                ", range_left='" + range_left + '\'' +
                ", range_right='" + range_right + '\'' +
                ", decimal_point='" + decimal_point + '\'' +
                ", example_value='" + example_value + '\'' +
                '}';
    }
}
