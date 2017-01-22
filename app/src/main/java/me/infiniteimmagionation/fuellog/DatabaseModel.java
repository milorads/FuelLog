package me.infiniteimmagionation.fuellog;

import java.util.Date;

/**
 * Created by msekulovic on 1/12/2017.
 */

public class DatabaseModel {
    //private variables
    int _id;
    String _tpl;
    long _date;
    float _cdop;
    long _km;
    long _lit;

    // constructor
    public DatabaseModel(int id, String totalOrPerLiter, long date, float refillPrice, long km, long liters){
        this._id = id;
        this._tpl = totalOrPerLiter;
        this._date = date;
        this._cdop = refillPrice;
        this._km = km;
        this._lit = liters;
    }
    public DatabaseModel(String totalOrPerLiter, long date, float refillPrice, long km, long liters){
        this._tpl = totalOrPerLiter;
        this._date = date;
        this._cdop = refillPrice;
        this._km = km;
        this._lit = liters;
    }

    public int get_id() {
        return _id;
    }

    public String get_tpl() {
        return _tpl;
    }

    public long get_date() {
        return _date;
    }

    public float get_cdop() {
        return _cdop;
    }

    public long get_km() {
        return _km;
    }

    public long get_lit(){return _lit;}
}
