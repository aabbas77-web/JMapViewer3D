/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oghab.mapviewer;

/**
 *
 * @author AZUS
 */
public class mv_stm {
    static public double _pi = 3.1415926535897932384626433832795d;
//    static public double _pi = Math.PI;
    
    static public String[] Convert_Geo_To_XY_String(double Lon,double Lat)
    {
        double[] res = mv_stm.Convert_Geo_To_XY(Lon, Lat);
        String strZone,strX,strY;
        strX = String.format("%.2f", res[0]);
        strY = String.format("%.2f", res[1]);
        strZone = Integer.toString((int)res[2]);
        
        String[] strRes = {strX, strY, strZone};
        return strRes;
    }
    
    static public double[] Convert_Geo_To_XY(double Lon,double Lat)
    {
        //ANGLE0
        //7 CLS: INPUT "DRGA(toL)"; A, "DKEK(toL)" ;C, "SANI(toL)";D
        //8 CLS: INPUT "DRGA(ard)"; H, "DKEK(ard)" ;K, "SANI(ard)�;S
        //9 L=DEG (A, C, D) :F= DEG(H, K, S)
        //10 DIM Z(6)
        //11 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
        //15 CLS: INPUT "RKM ALMATEKA"; I
        //16 IF I=0 OR I>6 THEN 15
        //18 DL=L-Z (I)
        //20 LR= DL*PI/180
        //30 N= 6377431.24/(SQR(1 -(0.00672267*((SINF) ^2))))
        //32 E= 0.00676817* ( (COSF) ^2)
        //34 B= 111119.87*F -16107.03 *SIN(2*F) +17.4 * SIN(4*F)
        //36 X= (LR*N*COSF)+((LR^3)/6)*N*((COSF)^3)*(1-((TANF) ^2) ) +E+200000
        //38 Y= B+ (LR^2/2) *N* (COSF) ^2*TANF+ (LR^4/24) *N* (COSF) ^4*TANF* (5
        //-	(TANF) ^2+9*E+4*E^2)
        //40 CLS: BEEP0: BEEP1: PRINT "X"; I; " = ";INTX, "Y";I; " = ";INTY

        int zone;
        double X,Y;
        double z[] = { 25.5, 28.5, 31.5, 34.5, 37.5, 40.5, 43.5 };
        double F = Lat * _pi / 180.0;

        if (Lon >= 24.0 && Lon < 27.0)
                zone = 1;
        else if (Lon >= 27.0 && Lon < 30.0)
                zone = 2;
        else if (Lon >= 30.0 && Lon < 33.0)
                zone = 3;
        else if (Lon >= 33.0 && Lon <= 37.5)
                zone = 4;
        else if (Lon > 37.5 && Lon < 39.0)
                zone = 5;
        else if (Lon >= 39.0 && Lon < 42.0)
                zone = 6;
        else
                zone = 7;

        // 2019.09.05 by Eng.Yamen Wassouf
        Lat += 0.29 / 3600.0;
        Lon -= 3.66 / 3600.0;

        double DL = Lon - z[zone - 1];
        double LR = DL * _pi / 180.0;

        //30 N= 6377431.24/(SQR(1 -(0.00672267*((SINF) ^2))))
        double N = 6377431.24 / Math.sqrt(1.0 - (0.00672267 * Math.pow(Math.sin(F), 2.0)));//��� ����� ���� ���

        //32 E= 0.00676817* ( (COSF) ^2)
        double E = 0.00676817 * Math.pow(Math.cos(F),2.0);

        //34 B= 111119.87*F -16107.03 *SIN(2*F) +17.4 * SIN(4*F)
        double B = 111119.87 * Lat - 16107.03 * Math.sin(2.0 * F) + 17.4 * Math.sin(4.0 * F);

        //36 X= (LR*N*COSF)+((LR^3)/6)*N*((COSF)^3)*(1-((TANF) ^2) ) +E+200000
        X = LR * N * Math.cos(F) + (Math.pow(LR, 3.0) / 6.0) * N * (Math.pow(Math.cos(F), 3.0)) * (1.0 - Math.pow(Math.tan(F), 2.0)) + E + 200000.0;

        //38 Y= B+ (LR^2/2) *N* (COSF) ^2*TANF+ (LR^4/24) *N* (COSF) ^4*TANF* (5 -	(TANF) ^2+9*E+4*E^2)
        Y = B + ((Math.pow(LR, 2.0) / 2.0) * N * Math.pow(Math.cos(F), 2.0) * Math.tan(F)) +
                ((Math.pow(LR, 4.0) / 24.0) * N * Math.pow(Math.cos(F), 4.0) * Math.tan(F) * (5.0 - Math.pow(Math.tan(F), 2.0) + 9.0 * E + 4.0 * Math.pow(E, 2.0)));
        double[] res = {X,Y,zone};
        return res;
    }

    static public double[] Convert_XY_To_Geo(double X,double Y,int zone)
    {
        //  2 CLS:INPUT "X= "; X, "Y= "; Y, "RKM AL MANTEKA= "; I
        //3 DIM Z(6)
        //4 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
        //5 X= (X- 200000)/0.99985: Y= Y/0.99985
        //6 FA=(Y- 3541905.65)/110934+32
        //8 BA=111136.54 * FA - 16107.04 * SIN (2*FA) +16. 97 * SIN (4*FA)
        //10 FA= (Y - BA)  / 110934 + FA
        //12 NA= 6378388 / SQR (1 - 0.00672267 * (SINFA) ^2)
        //14 E= 0.00676817 * ((COSFA) ^2)
        //16 C=((X/NA) ^2/2) * TANFA*(1+E) : D=(X/NA)^4/24*(TANFA*(5
        //+3*TANFA ^2 +6*E -6*E* (TANFA) ^2 -3*E ^2 -9*TANFA ^2*E ^2))
        //18 F=FA -(C -D)*180/PI
        //20 O=  X/(NA*COSFA) : P=X^3/(6*NA^3*COSFA)*(1+2*(TANFA)^2+E)
        //22 L= (O- P)*180/PI+ Z(I)
        //24 CLS: PRINT "DEG(TOL) "; I; " = "DMS$(L), "DEG(ARD)"; I; "="DMS$ (F)

        double lon,lat;
        
        // 2019.09.05 by Eng.Yamen Wassouf
        X += 95.0;
        Y -= 8.0;

        //4 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
        double z[] = { 25.5, 28.5, 31.5, 34.5, 37.5, 40.5, 43.5 };

        //5 X= (X- 200000)/0.99985: Y= Y/0.99985
        X = (X - 200000.0) / 0.99985;
        Y = Y / 0.99985;

        //6 FA=(Y- 3541905.65)/110934+32
        double FA = (Y - 3541905.65) / 110934.0 + 32.0;
        double FA1 = FA * _pi / 180.0;

        //8 BA=111136.54 * FA - 16107.04 * SIN (2*FA) +16. 97 * SIN (4*FA)
        double BA = 111136.54 * FA - 16107.04 * Math.sin(2.0 * FA1) + 16.97 * Math.sin(4.0 * FA1);

        //10 FA= (Y - BA)  / 110934 + FA
        FA = (Y - BA) / 110934.0 + FA;
        FA1 = FA * _pi / 180.0;

        //12 NA= 6378388 / SQR (1 - 0.00672267 * (SINFA) ^2)
        double NA = 6378388.0 / Math.sqrt(1.0 - 0.00672267 * Math.pow(Math.sin(FA1), 2.0));

        //14 E= 0.00676817 * ((COSFA) ^2)
        double E = 0.00676817 * Math.pow(Math.cos(FA1), 2.0);

        //16 C=((X/NA) ^2/2) * TANFA*(1+E)
        double C = (Math.pow((X / NA), 2.0) / 2.0) * Math.tan(FA1) * (1.0 + E);

        //: D=(X/NA)^4/24*(TANFA*(5
        //+3*TANFA ^2 +6*E -6*E* (TANFA) ^2 -3*E ^2 -9*TANFA ^2*E ^2))
        double D = (Math.pow((X / NA), 4.0) / 24.0) * (Math.tan(FA1) * (5.0
         + (3.0 * Math.pow(Math.tan(FA1), 2.0)) + (6.0 * E) - (6.0 * E * Math.pow(Math.tan(FA1), 2.0))
         - (3.0 * Math.pow(E, 2.0)) - (9.0 * Math.pow(Math.tan(FA1), 2.0) * Math.pow(E, 2.0))));

        //18 F=FA -(C -D)*180/PI
        lat = FA - (C - D) * 180.0 / _pi;

        //20 O=  X/(NA*COSFA) : P=X^3/(6*NA^3*COSFA)*(1+2*(TANFA)^2+E)
        double O = X / (NA * Math.cos(FA1));
        double P = Math.pow(X, 3.0) / (6.0 * Math.pow(NA, 3.0) * Math.cos(FA1)) * (1.0 + 2.0 * Math.pow(Math.tan(FA1), 2.0) + E);

        //22 L= (O- P)*180/PI+ Z(I)
        lon = (O - P) * 180.0 / _pi + z[zone - 1];

        double[] res = {lon,lat};
        return res;
    }
    
//    static public void Convert_Geo_To_XY(double Lon,double Lat,int zone,double X,double Y)
//    {
//            //ANGLE0
//            //7 CLS: INPUT "DRGA(toL)"; A, "DKEK(toL)" ;C, "SANI(toL)";D
//            //8 CLS: INPUT "DRGA(ard)"; H, "DKEK(ard)" ;K, "SANI(ard)�;S
//            //9 L=DEG (A, C, D) :F= DEG(H, K, S)
//            //10 DIM Z(6)
//            //11 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
//            //15 CLS: INPUT "RKM ALMATEKA"; I
//            //16 IF I=0 OR I>6 THEN 15
//            //18 DL=L-Z (I)
//            //20 LR= DL*PI/180
//            //30 N= 6377431.24/(SQR(1 -(0.00672267*((SINF) ^2))))
//            //32 E= 0.00676817* ( (COSF) ^2)
//            //34 B= 111119.87*F -16107.03 *SIN(2*F) +17.4 * SIN(4*F)
//            //36 X= (LR*N*COSF)+((LR^3)/6)*N*((COSF)^3)*(1-((TANF) ^2) ) +E+200000
//            //38 Y= B+ (LR^2/2) *N* (COSF) ^2*TANF+ (LR^4/24) *N* (COSF) ^4*TANF* (5
//            //-	(TANF) ^2+9*E+4*E^2)
//            //40 CLS: BEEP0: BEEP1: PRINT "X"; I; " = ";INTX, "Y";I; " = ";INTY
//
//            double z[] = { 25.5d, 28.5d, 31.5d, 34.5d, 37.5d, 40.5d, 43.5d };
//            double F = Lat * _pi / 180.0d;
//
//            if (Lon >= 24.0d && Lon < 27.0d)
//                    zone = 1;
//            else if (Lon >= 27.0d && Lon < 30.0d)
//                    zone = 2;
//            else if (Lon >= 30.0d && Lon < 33.0d)
//                    zone = 3;
//            else if (Lon >= 33.0d && Lon <= 37.5d)
//                    zone = 4;
//            else if (Lon > 37.5d && Lon < 39.0d)
//                    zone = 5;
//            else if (Lon >= 39.0d && Lon < 42.0d)
//                    zone = 6;
//            else
//                    zone = 7;
//
//            // 2019.09.05 by Eng.Yamen Wassouf
//            Lat += 0.29d / 3600.0d;
//            Lon -= 3.66d / 3600.0d;
//
//            double DL = Lon - z[zone - 1];
//            double LR = DL * _pi / 180.0d;
//
//            //30 N= 6377431.24/(SQR(1 -(0.00672267*((SINF) ^2))))
//            double N = 6377431.24d / Math.sqrt(1.0d - (0.00672267d * Math.pow(Math.sin(F), 2.0d)));//��� ����� ���� ���
//
//            //32 E= 0.00676817* ( (COSF) ^2)
//            double E = 0.00676817d * Math.pow(Math.cos(F),2.0d);
//
//            //34 B= 111119.87*F -16107.03 *SIN(2*F) +17.4 * SIN(4*F)
//            double B = 111119.87d * Lat - 16107.03d * Math.sin(2.0d * F) + 17.4d * Math.sin(4.0d * F);
//
//            //36 X= (LR*N*COSF)+((LR^3)/6)*N*((COSF)^3)*(1-((TANF) ^2) ) +E+200000
//            X = LR * N * Math.cos(F) + (Math.pow(LR, 3.0d) / 6.0d) * N * (Math.pow(Math.cos(F), 3.0d)) * (1.0d - Math.pow(Math.tan(F), 2.0d)) + E + 200000.0d;
//
//            //38 Y= B+ (LR^2/2) *N* (COSF) ^2*TANF+ (LR^4/24) *N* (COSF) ^4*TANF* (5 -	(TANF) ^2+9*E+4*E^2)
//            Y = B + ((Math.pow(LR, 2.0d) / 2.0d) * N * Math.pow(Math.cos(F), 2.0d) * Math.tan(F)) +
//                    ((Math.pow(LR, 4.0d) / 24.0d) * N * Math.pow(Math.cos(F), 4.0d) * Math.tan(F) * (5.0d - Math.pow(Math.tan(F), 2.0d) + 9.0d * E + 4.0d * Math.pow(E, 2.0d)));
//    }
//
//    static public void Convert_XY_To_Geo(double X,double Y,int zone,double lon,double lat)
//    {
//            //  2 CLS:INPUT "X= "; X, "Y= "; Y, "RKM AL MANTEKA= "; I
//            //3 DIM Z(6)
//            //4 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
//            //5 X= (X- 200000)/0.99985: Y= Y/0.99985
//            //6 FA=(Y- 3541905.65)/110934+32
//            //8 BA=111136.54 * FA - 16107.04 * SIN (2*FA) +16. 97 * SIN (4*FA)
//            //10 FA= (Y - BA)  / 110934 + FA
//            //12 NA= 6378388 / SQR (1 - 0.00672267 * (SINFA) ^2)
//            //14 E= 0.00676817 * ((COSFA) ^2)
//            //16 C=((X/NA) ^2/2) * TANFA*(1+E) : D=(X/NA)^4/24*(TANFA*(5
//            //+3*TANFA ^2 +6*E -6*E* (TANFA) ^2 -3*E ^2 -9*TANFA ^2*E ^2))
//            //18 F=FA -(C -D)*180/PI
//            //20 O=  X/(NA*COSFA) : P=X^3/(6*NA^3*COSFA)*(1+2*(TANFA)^2+E)
//            //22 L= (O- P)*180/PI+ Z(I)
//            //24 CLS: PRINT "DEG(TOL) "; I; " = "DMS$(L), "DEG(ARD)"; I; "="DMS$ (F)
//
//            // 2019.09.05 by Eng.Yamen Wassouf
//            X += 95.0d;
//            Y -= 8.0d;
//
//            //4 Z(1)=25.5: Z(2)=28.5: Z(3)=31.5: Z(4)=34.5: Z(5)=37.5: Z(6)=40.5
//            double z[] = { 25.5d, 28.5d, 31.5d, 34.5d, 37.5d, 40.5d, 43.5d };
//
//            //5 X= (X- 200000)/0.99985: Y= Y/0.99985
//            X = (X - 200000.0d) / 0.99985d;
//            Y = Y / 0.99985d;
//
//            //6 FA=(Y- 3541905.65)/110934+32
//            double FA = (Y - 3541905.65d) / 110934.0d + 32.0d;
//            double FA1 = FA * _pi / 180.0d;
//
//            //8 BA=111136.54 * FA - 16107.04 * SIN (2*FA) +16. 97 * SIN (4*FA)
//            double BA = 111136.54d * FA - 16107.04d * Math.sin(2.0d * FA1) + 16.97d * Math.sin(4.0d * FA1);
//
//            //10 FA= (Y - BA)  / 110934 + FA
//            FA = (Y - BA) / 110934.0d + FA;
//            FA1 = FA * _pi / 180.0d;
//
//            //12 NA= 6378388 / SQR (1 - 0.00672267 * (SINFA) ^2)
//            double NA = 6378388.0d / Math.sqrt(1.0d - 0.00672267d * Math.pow(Math.sin(FA1), 2.0d));
//
//            //14 E= 0.00676817 * ((COSFA) ^2)
//            double E = 0.00676817d * Math.pow(Math.cos(FA1), 2.0d);
//
//            //16 C=((X/NA) ^2/2) * TANFA*(1+E)
//            double C = (Math.pow((X / NA), 2.0d) / 2.0d) * Math.tan(FA1) * (1.0d + E);
//
//            //: D=(X/NA)^4/24*(TANFA*(5
//            //+3*TANFA ^2 +6*E -6*E* (TANFA) ^2 -3*E ^2 -9*TANFA ^2*E ^2))
//            double D = (Math.pow((X / NA), 4.0d) / 24.0d) * (Math.tan(FA1) * (5.0d
//             + (3.0d * Math.pow(Math.tan(FA1), 2.0d)) + (6.0d * E) - (6.0d * E * Math.pow(Math.tan(FA1), 2.0d))
//             - (3.0d * Math.pow(E, 2.0d)) - (9.0d * Math.pow(Math.tan(FA1), 2.0d) * Math.pow(E, 2.0d))));
//
//            //18 F=FA -(C -D)*180/PI
//            lat = FA - (C - D) * 180.0d / _pi;
//
//            //20 O=  X/(NA*COSFA) : P=X^3/(6*NA^3*COSFA)*(1+2*(TANFA)^2+E)
//            double O = X / (NA * Math.cos(FA1));
//            double P = Math.pow(X, 3.0d) / (6.0d * Math.pow(NA, 3.0d) * Math.cos(FA1)) * (1.0d + 2.0d * Math.pow(Math.tan(FA1), 2.0d) + E);
//
//            //22 L= (O- P)*180/PI+ Z(I)
//            lon = (O - P) * 180.0d / _pi + z[zone - 1];
//    }
    
}
