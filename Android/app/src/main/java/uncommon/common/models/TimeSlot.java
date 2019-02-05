package uncommon.common.models;

public class TimeSlot {

    String selectDate = "";
    boolean selected = false;

    public TimeSlot(String date , boolean select){
        this.selectDate = date;
        this.selected = select;
    }

    public void setSelectDate(String date){
        selectDate = date;
    }
    public void setSelected (boolean select){
        selected = select;
    }

    public String getSelectDate(){
        return this.selectDate;
    }

    public  boolean getSeleced(){
        return this.selected;
    }
}
