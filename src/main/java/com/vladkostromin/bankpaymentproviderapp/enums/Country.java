package com.vladkostromin.bankpaymentproviderapp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Country {
    AF("AF"), AL("AL"), DZ("DZ"), AS("AS"), AD("AD"), AO("AO"),
    AI("AI"), AQ("AQ"), AG("AG"), AR("AR"), AM("AM"), AW("AW"),
    AU("AU"), AT("AT"), AZ("AZ"), BS("BS"), BH("BH"), BD("BD"),
    BB("BB"), BY("BY"), BE("BE"), BZ("BZ"), BJ("BJ"), BM("BM"),
    BT("BT"), BO("BO"), BA("BA"), BW("BW"), BR("BR"), IO("IO"),
    BN("BN"), BG("BG"), BF("BF"), BI("BI"), CV("CV"), KH("KH"),
    CM("CM"), CA("CA"), KY("KY"), CF("CF"), TD("TD"), CL("CL"),
    CN("CN"), CO("CO"), KM("KM"), CG("CG"), CD("CD"), CK("CK"),
    CR("CR"), CI("CI"), HR("HR"), CU("CU"), CY("CY"), CZ("CZ"),
    DK("DK"), DJ("DJ"), DM("DM"), DO("DO"), EC("EC"), EG("EG"),
    SV("SV"), GQ("GQ"), ER("ER"), EE("EE"), SZ("SZ"), ET("ET"),
    FJ("FJ"), FI("FI"), FR("FR"), GF("GF"), PF("PF"), GA("GA"),
    GM("GM"), GE("GE"), DE("DE"), GH("GH"), GI("GI"), GR("GR"),
    GL("GL"), GD("GD"), GP("GP"), GU("GU"), GT("GT"), GN("GN"),
    GW("GW"), GY("GY"), HT("HT"), HN("HN"), HK("HK"), HU("HU"),
    IS("IS"), IN("IN"), ID("ID"), IR("IR"), IQ("IQ"), IE("IE"),
    IL("IL"), IT("IT"), JM("JM"), JP("JP"), JO("JO"), KZ("KZ"),
    KE("KE"), KI("KI"), KP("KP"), KR("KR"), KW("KW"), KG("KG"),
    LA("LA"), LV("LV"), LB("LB"), LS("LS"), LR("LR"), LY("LY"),
    LI("LI"), LT("LT"), LU("LU"), MO("MO"), MG("MG"), MW("MW"),
    MY("MY"), MV("MV"), ML("ML"), MT("MT"), MH("MH"), MQ("MQ"),
    MR("MR"), MU("MU"), YT("YT"), MX("MX"), FM("FM"), MD("MD"),
    MC("MC"), MN("MN"), ME("ME"), MS("MS"), MA("MA"), MZ("MZ"),
    MM("MM"), NA("NA"), NR("NR"), NP("NP"), NL("NL"), NC("NC"),
    NZ("NZ"), NI("NI"), NE("NE"), NG("NG"), NU("NU"), NF("NF"),
    MP("MP"), NO("NO"), OM("OM"), PK("PK"), PW("PW"), PA("PA"),
    PG("PG"), PY("PY"), PE("PE"), PH("PH"), PL("PL"), PT("PT"),
    PR("PR"), QA("QA"), MK("MK"), RO("RO"), RU("RU"), RW("RW"),
    RE("RE"), BL("BL"), SH("SH"), KN("KN"), LC("LC"), MF("MF"),
    PM("PM"), VC("VC"), WS("WS"), SM("SM"), ST("ST"), SA("SA"),
    SN("SN"), RS("RS"), SC("SC"), SL("SL"), SG("SG"), SX("SX"),
    SK("SK"), SI("SI"), SB("SB"), SO("SO"), ZA("ZA"), SS("SS"),
    ES("ES"), LK("LK"), SD("SD"), SR("SR"), SE("SE"), CH("CH"),
    SY("SY"), TW("TW"), TJ("TJ"), TZ("TZ"), TH("TH"), TL("TL"),
    TG("TG"), TK("TK"), TO("TO"), TT("TT"), TN("TN"), TR("TR"),
    TM("TM"), TC("TC"), TV("TV"), UG("UG"), UA("UA"), AE("AE"),
    GB("GB"), US("US"), UY("UY"), UZ("UZ"), VU("VU"), VA("VA"),
    VE("VE"), VN("VN"), VG("VG"), VI("VI"), WF("WF"), EH("EH"),
    YE("YE"), ZM("ZM"), ZW("ZW");

    private final String countryCode;

    Country(String countryCode) {
        this.countryCode = countryCode;
    }
    @JsonCreator
    public static Country fromString(String countryCode) {
        for (Country country : Country.values()) {
            if (country.getCountryCode().equals(countryCode)) {
                return country;
            }
        }
        throw new IllegalArgumentException(countryCode);
    }

    @JsonValue
    public String countryCode() {
        return this.countryCode;
    }
    @Override
    public String toString() {
        return this.countryCode;
    }
}
