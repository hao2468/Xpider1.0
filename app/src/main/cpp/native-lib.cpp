#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <cmath>
using namespace cv;
using namespace std;
extern "C" {
JNIEXPORT jint JNICALL
Java_com_example_hao_xpider10_Mycpp_getlines(JNIEnv *env, jobject /* this */, jlong addsrc) {
    Mat &msrc = *(Mat *) addsrc;
    Mat ROI = msrc(Rect(msrc.cols / 4, msrc.rows * 2/5, msrc.cols / 2, msrc.rows * 3 / 5));
    Mat g;

    medianBlur(ROI, g, 3);
    blur(g, g, Size(3, 3));
    cvtColor(g, g, CV_BGR2GRAY);
    vector<Point2f>corners;
    goodFeaturesToTrack(g, corners, 100, 0.1, 10, noArray(), 3, false);
    Canny(g, g, 50, 100);
    vector<Vec4i> lines;
    HoughLinesP(g, lines, CV_PI / 180, 1, 3, 0, 0);
    Vec4i l;
    for (int i = 0; i < lines.size(); i++) {
        for (int j = i + 1; j < lines.size() - 1; j++) {
            double x1 = lines[i][0] - lines[j][0];
            double x2 = lines[i][2] - lines[j][2];
            if ((x1 > -5 && x1 < 5) && (x2 > -5 && x2 < 5)) {
                lines.erase(lines.begin() + j - 1);
            }
        }
    }
    jint flag = int(lines.size()*0.7)+corners.size();
    return flag;
}

JNIEXPORT jdoubleArray JNICALL
Java_com_example_hao_xpider10_Mycpp_getcircle(JNIEnv *env, jobject, jlong addsrc,jint lowh,jint lows,jint lowv,jint highh,jboolean testflag) {
    Mat &msrc = *(Mat *) addsrc;
    Mat g  ;
    msrc.copyTo(g);
    //jint find = 0;
    jdouble ins[2];
    jdoubleArray doublearray = env->NewDoubleArray(2);
    //resize(g,g, Size(msrc.cols /5,msrc.rows / 5));

    /*int lowh = 41;
    int lows = 88;
    int lowv = 71;
    int highh = 86;*/
    int highs = 255;
    int highv = 255;
    int circleflag = 50;
    double x;
    double y;
    Mat hsv;
    vector<Mat> hsvSplit;
    cvtColor(msrc, hsv,COLOR_BGR2HSV);
    split(hsv, hsvSplit);
    equalizeHist(hsvSplit[2], hsvSplit[2]);
    merge(hsvSplit, hsv);
    Mat t;
    inRange(hsv, Scalar(lowh, lows, lowv), Scalar(highh, highs, highv), t);
    Mat element = getStructuringElement(MORPH_RECT, Size(3,3));
    morphologyEx(t,t, MORPH_OPEN, element);
    morphologyEx(t,t, MORPH_CLOSE, element);

    vector<vector<Point>>contours;
    vector<Vec4i>hierarchy;
    findContours(t, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE);
    vector<Point>maxcontour;
    double maxArea = 0;
    if (contours.size() > 0) {
        for (int i = 0; i < contours.size(); i++) {
            double area = contourArea(contours[i]);
            if (maxArea < area)
            {
                maxArea = area;
                maxcontour = contours[i];
            }
        }
        double maxX = maxcontour[0].x;
        double minX = maxcontour[0].x;
        double maxY = maxcontour[0].y;
        double minY = maxcontour[0].y;
        for (int i = 1; i < maxcontour.size(); i++)
        {
            if (maxX < maxcontour[i].x) {
                maxX = maxcontour[i].x;
            }
            if (minX > maxcontour[i].x) {
                minX = maxcontour[i].x;
            }
            if (maxY < maxcontour[i].y) {
                maxY = maxcontour[i].y;
            }
            if (minY > maxcontour[i].y) {
                minY = maxcontour[i].y;
            }
        }
        x = (maxX + minX)/2;
        y = (maxY + minY)/2;
        circle(msrc, Point(x, y), 5, Scalar(150, 30, 100), 2);
    }


    if (testflag){
        msrc = t;
    }

    jdouble xdistance = 0;
    jdouble ydistance = 0;
    if (contours.size() != 0) {
        xdistance = x-msrc.cols/2;
        ydistance = y-msrc.rows/2;
    }

    ins[0] = xdistance;
    ins[1] = ydistance;
    env->SetDoubleArrayRegion(doublearray,0,2,ins);

    return doublearray;
}

JNIEXPORT jintArray JNICALL
Java_com_example_hao_xpider10_Mycpp_getsquare(JNIEnv *env, jobject, jlong addsrc) {
    Mat &msrc = *(Mat *) addsrc;
    jintArray array1 = env->NewIntArray(2);
    jint find = 0;
    jint ins[2];
   // Mat &mtest = *(Mat *) addtest;
    Mat chuli;
    //blur(msrc, chuli, Size(3, 3));
    //medianBlur(msrc, chuli, 3);
    cvtColor(msrc, chuli, CV_BGR2GRAY);
    GaussianBlur(chuli,chuli,Size(5,5),0,0);
    adaptiveThreshold(chuli, chuli, 255, ADAPTIVE_THRESH_MEAN_C, 0, 3, 0.1);
    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    findContours(chuli, contours, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE, Point(0, 0));
    vector<vector<Point>> contours_poly(contours.size());
    double line[10];
    double linex[10];
    double liney[10];
    int lineflag = 0;
    int fang = 0;
    for (int i = 0; i < contours.size(); i++) {
        approxPolyDP(Mat(contours[i]), contours_poly[i], 20, true);
    }
    double f[15];
    double x[6];
    double xx[6];
    double y[6];
    double yy[6];
    int flag = 0;
    double d[6];
    for (int i = 0; i < contours.size(); i++) {
        if (contours_poly[i].size() == 4) {
            flag = 0;
            for (int n = 0; n < 4; n++) {
                for (int m = n + 1; m < 4; m++) {
                    x[flag] = contours_poly[i][n].x - contours_poly[i][m].x;
                    xx[flag] = (contours_poly[i][n].x + contours_poly[i][m].x) / 2;
                    y[flag] = contours_poly[i][n].y - contours_poly[i][m].y;
                    yy[flag] = (contours_poly[i][n].y + contours_poly[i][m].y) / 2;
                    d[flag] = sqrt(pow(x[flag], 2) + pow(y[flag], 2));
                    flag++;
                }
            }
            flag = 0;
            double max = d[0];
            double min = d[0];
            for (int n = 1; n < 6; n++) {
                if (max < d[n]) {
                    max = d[n];
                    flag++;
                }
                if (min > d[n]) {
                    min = d[n];
                }
            }
            if (max / min > 5) {
                continue;
            }
            linex[lineflag] = abs(xx[flag]);
            liney[lineflag] = abs(yy[flag]);
            line[lineflag] = max;
            lineflag++;
            flag = 0;
            for (int n = 0; n < 5; n++) {
                for (int m = n + 1; m < 6; m++) {
                    f[flag] = x[n] * x[m] + y[n] * y[m];
                    flag++;
                }
            }
            flag = 0;
            for (int x = 0; x < 15; x++) {
                if (f[x] > -150 && f[x] < 150) {
                    flag++;
                }
            }
            if (flag > 3 || flag == 3) {
                drawContours(msrc, contours_poly, i, Scalar(150, 30, 100), 2, 8, vector<Vec4i>(), 0,
                             Point());
                find++;
            }
            flag = 0;
        }
    }
    double linemax = line[0];
    for (int n = 0; n < lineflag; n++) {
        if (linemax < line[n]) {
            linemax = line[n];
            flag++;
        }
    }
    if (linex[flag] != msrc.cols / 2) {
        if (linex[flag] < msrc.cols / 2) {
            if (liney[flag] != msrc.rows *3/5) {
                if (liney[flag] < msrc.rows *3/5) {
                    fang = -10;
                } else {
                    fang = -11;
                }
            } else {
                fang = -1;
            }
        } else {
            if (liney[flag] != msrc.rows *3/5) {
                if (liney[flag] < msrc.rows *3/5) {
                    fang = 10;
                } else {
                    fang = 11;
                }
            } else {
                fang = 1;
            }
        }
    }
    Point2f pt(msrc.cols / 2, msrc.rows / 2);
    Mat rr = getRotationMatrix2D(pt, 270, 1.0);
    warpAffine(msrc, msrc, rr, msrc.size());
    ins[0] = fang;
    ins[1] = find;
    env->SetIntArrayRegion(array1,0,2,ins);
    return array1;
}
}


