package edu.umass.cs.accelerometer;

public class ActivityClassifier {

	private static double RtoP(double[] R, int j) {
		double Rcenter = 0;
		for (int i = 0; i < R.length; i++) {
			Rcenter += R[i];
		}
		Rcenter /= R.length;
		double Rsum = 0;
		for (int i = 0; i < R.length; i++) {
			Rsum += Math.exp(R[i] - Rcenter);
		}
		return Math.exp(R[j]) / Rsum;
	}

	public static double classify(Object[] i) {
		double[] d = distribution(i);
		double maxV = d[0];
		int maxI = 0;
		for (int j = 1; j < 3; j++) {
			if (d[j] > maxV) {
				maxV = d[j];
				maxI = j;
			}
		}
		return (double) maxI;
	}

	public static double[] distribution(Object[] i) {
		double[] Fs = new double[3];
		double[] Fi = new double[3];
		double Fsum;
		Fsum = 0;
		Fi[0] = WekaClassifier_0_0.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_0.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_0.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_1.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_1.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_1.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_2.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_2.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_2.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_3.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_3.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_3.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_4.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_4.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_4.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_5.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_5.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_5.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_6.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_6.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_6.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_7.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_7.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_7.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_8.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_8.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_8.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		Fsum = 0;
		Fi[0] = WekaClassifier_0_9.classify(i);
		Fsum += Fi[0];
		Fi[1] = WekaClassifier_1_9.classify(i);
		Fsum += Fi[1];
		Fi[2] = WekaClassifier_2_9.classify(i);
		Fsum += Fi[2];
		Fsum /= 3;
		for (int j = 0; j < 3; j++) {
			Fs[j] += (Fi[j] - Fsum) * 2 / 3;
		}
		double[] dist = new double[3];
		for (int j = 0; j < 3; j++) {
			dist[j] = RtoP(Fs, j);
		}
		return dist;
	}
}

class WekaClassifier_0_0 {
	public static double classify(Object[] i) {
		/* xFFT2 */
		if (i[4] == null) {
			return -0.27787769784172484;
		} else if (((Double) i[4]).doubleValue() <= 6.328125) {
			return -1.1707317073170687;
		} else {
			return 1.483957219251328;
		}
	}
}

class WekaClassifier_0_1 {
	public static double classify(Object[] i) {
		/* zDistance */
		if (i[26] == null) {
			return -0.04326704134708377;
		} else if (((Double) i[26]).doubleValue() <= 46831.529367172865) {
			return -0.8524283078161151;
		} else {
			return 0.7461737302445814;
		}
	}
}

class WekaClassifier_0_2 {
	public static double classify(Object[] i) {
		/* speedDev */
		if (i[28] == null) {
			return 0.014354512081445205;
		} else if (((Double) i[28]).doubleValue() <= 0.7144802107501516) {
			return -1.1396648986354987;
		} else {
			return 0.5576352531546518;
		}
	}
}

class WekaClassifier_0_3 {
	public static double classify(Object[] i) {
		/* xCrossRate */
		if (i[2] == null) {
			return -0.10318221423501824;
		} else if (((Double) i[2]).doubleValue() <= 0.13636363636363635) {
			return 0.3912902705099656;
		} else {
			return -0.7190650626595068;
		}
	}
}

class WekaClassifier_0_4 {
	public static double classify(Object[] i) {
		/* energyFFT3 */
		if (i[38] == null) {
			return -0.02297451233014467;
		} else if (((Double) i[38]).doubleValue() <= 361.4752098319322) {
			return -0.976871364059448;
		} else {
			return 0.3614355593866438;
		}
	}
}

class WekaClassifier_0_5 {
	public static double classify(Object[] i) {
		/* zDev */
		if (i[19] == null) {
			return -0.14330285037885637;
		} else if (((Double) i[19]).doubleValue() <= 1.1101085256115524) {
			return -1.220410367170605;
		} else {
			return 0.12498758414328329;
		}
	}
}

class WekaClassifier_0_6 {
	public static double classify(Object[] i) {
		/* energyFFT3 */
		if (i[38] == null) {
			return -0.014493381057802334;
		} else if (((Double) i[38]).doubleValue() <= 89.37755612311727) {
			return 2.647293261354454;
		} else {
			return -0.10199291932102364;
		}
	}
}

class WekaClassifier_0_7 {
	public static double classify(Object[] i) {
		/* energyDev */
		if (i[34] == null) {
			return 0.10013324688599648;
		} else if (((Double) i[34]).doubleValue() <= 1.2772639186408314) {
			return -0.8810154665494824;
		} else {
			return 0.3378976417970465;
		}
	}
}

class WekaClassifier_0_8 {
	public static double classify(Object[] i) {
		/* zDistance */
		if (i[26] == null) {
			return -0.06429663322602529;
		} else if (((Double) i[26]).doubleValue() <= 46461.84391609371) {
			return -0.966789395523224;
		} else {
			return 0.24520071044560945;
		}
	}
}

class WekaClassifier_0_9 {
	public static double classify(Object[] i) {
		/* yDev */
		if (i[10] == null) {
			return 0.018690201389126936;
		} else if (((Double) i[10]).doubleValue() <= 1.2692557681971044) {
			return 0.20329939141902878;
		} else {
			return -1.1072069989011364;
		}
	}
}

class WekaClassifier_1_0 {
	public static double classify(Object[] i) {
		/* energyDev */
		if (i[34] == null) {
			return 0.4667266187050192;
		} else if (((Double) i[34]).doubleValue() <= 0.9334941813152888) {
			return 2.7220077220077092;
		} else {
			return -1.5000000000000113;
		}
	}
}

class WekaClassifier_1_1 {
	public static double classify(Object[] i) {
		/* zDev */
		if (i[19] == null) {
			return -0.12718846053860597;
		} else if (((Double) i[19]).doubleValue() <= 0.6276968782288439) {
			return 0.7230341815869977;
		} else {
			return -1.1904632446847112;
		}
	}
}

class WekaClassifier_1_2 {
	public static double classify(Object[] i) {
		/* energyMean */
		if (i[33] == null) {
			return -0.1560352887168015;
		} else if (((Double) i[33]).doubleValue() <= 9.392582895109706) {
			return 0.8541946836272314;
		} else {
			return -0.8298987796509765;
		}
	}
}

class WekaClassifier_1_3 {
	public static double classify(Object[] i) {
		/* xDev */
		if (i[1] == null) {
			return -0.23326075472446675;
		} else if (((Double) i[1]).doubleValue() <= 0.16721427816091244) {
			return 0.4948993126521912;
		} else {
			return -0.9931252560418918;
		}
	}
}

class WekaClassifier_1_4 {
	public static double classify(Object[] i) {
		/* energyMean */
		if (i[33] == null) {
			return -0.06884086521850812;
		} else if (((Double) i[33]).doubleValue() <= 9.439032464464827) {
			return 0.7717080126432304;
		} else {
			return -0.9775133957942941;
		}
	}
}

class WekaClassifier_1_5 {
	public static double classify(Object[] i) {
		/* xFFT4 */
		if (i[6] == null) {
			return 0.11604934124871466;
		} else if (((Double) i[6]).doubleValue() <= 18.28125) {
			return 0.6727086303716692;
		} else {
			return -0.5663359165711482;
		}
	}
}

class WekaClassifier_1_6 {
	public static double classify(Object[] i) {
		/* speedMean */
		if (i[27] == null) {
			return -0.1578529417330215;
		} else if (((Double) i[27]).doubleValue() <= 0.08562739229000721) {
			return 0.5853387921557576;
		} else {
			return -0.6390312247562635;
		}
	}
}

class WekaClassifier_1_7 {
	public static double classify(Object[] i) {
		/* energyXYMean */
		if (i[40] == null) {
			return -0.15346474343308403;
		} else if (((Double) i[40]).doubleValue() <= 0.05603827450792942) {
			return -1.779400171336884;
		} else {
			return 0.08995830842167553;
		}
	}
}

class WekaClassifier_1_8 {
	public static double classify(Object[] i) {
		/* energyFFT1 */
		if (i[36] == null) {
			return 0.08326400573135713;
		} else if (((Double) i[36]).doubleValue() <= 738.9025090662269) {
			return 0.44388073854232196;
		} else {
			return -1.120426240103632;
		}
	}
}

class WekaClassifier_1_9 {
	public static double classify(Object[] i) {
		/* speedMean */
		if (i[27] == null) {
			return -0.06738117041125423;
		} else if (((Double) i[27]).doubleValue() <= 0.09945521964521717) {
			return 0.6388811294457234;
		} else {
			return -0.6683050092491882;
		}
	}
}

class WekaClassifier_2_0 {
	public static double classify(Object[] i) {
		/* zDev */
		if (i[19] == null) {
			return -0.18884892086330568;
		} else if (((Double) i[19]).doubleValue() <= 2.400597056764315) {
			return -0.9741573033707863;
		} else {
			return 2.9594594594594867;
		}
	}
}

class WekaClassifier_2_1 {
	public static double classify(Object[] i) {
		/* xDev */
		if (i[1] == null) {
			return 0.16180281300491575;
		} else if (((Double) i[1]).doubleValue() <= 1.037489743286757) {
			return -0.3286312821069525;
		} else {
			return 1.2866548270720575;
		}
	}
}

class WekaClassifier_2_2 {
	public static double classify(Object[] i) {
		/* xDistance */
		if (i[8] == null) {
			return 0.10048440054208294;
		} else if (((Double) i[8]).doubleValue() <= 9637.099998911102) {
			return 2.7331376456664356;
		} else {
			return 9.393552628644855E-4;
		}
	}
}

class WekaClassifier_2_3 {
	public static double classify(Object[] i) {
		/* zVelocityChange */
		if (i[25] == null) {
			return 0.22198567262781008;
		} else if (((Double) i[25]).doubleValue() <= 46516.605279101124) {
			return 1.0306283460485932;
		} else {
			return -0.10753914664341596;
		}
	}
}

class WekaClassifier_2_4 {
	public static double classify(Object[] i) {
		/* xFFT1 */
		if (i[3] == null) {
			return 0.05259930755338436;
		} else if (((Double) i[3]).doubleValue() <= 4.3997985701034565) {
			return 1.4059204321202763;
		} else {
			return -0.15014054903470295;
		}
	}
}

class WekaClassifier_2_5 {
	public static double classify(Object[] i) {
		/* energyDev */
		if (i[34] == null) {
			return 0.056188947432055006;
		} else if (((Double) i[34]).doubleValue() <= 2.140132340332692) {
			return -0.2024996384301369;
		} else {
			return 1.1166908429621611;
		}
	}
}

class WekaClassifier_2_6 {
	public static double classify(Object[] i) {
		/* xFFT2 */
		if (i[4] == null) {
			return 0.08462894244213795;
		} else if (((Double) i[4]).doubleValue() <= 40.78125) {
			return -0.32172454839479675;
		} else {
			return 0.6339337161979887;
		}
	}
}

class WekaClassifier_2_7 {
	public static double classify(Object[] i) {
		/* xFFT1 */
		if (i[3] == null) {
			return -0.01743527911195846;
		} else if (((Double) i[3]).doubleValue() <= 6.378204473209465) {
			return 1.0787362041654325;
		} else {
			return -0.19971721974735918;
		}
	}
}

class WekaClassifier_2_8 {
	public static double classify(Object[] i) {
		/* xDev */
		if (i[1] == null) {
			return 0.011276674240054097;
		} else if (((Double) i[1]).doubleValue() <= 0.9873513201437167) {
			return -0.2935315930249171;
		} else {
			return 0.5749766433761018;
		}
	}
}

class WekaClassifier_2_9 {
	public static double classify(Object[] i) {
		/* energyDev */
		if (i[34] == null) {
			return 0.015508622003030966;
		} else if (((Double) i[34]).doubleValue() <= 2.5416008964762913) {
			return -0.10287100114595531;
		} else {
			return 1.108126914983277;
		}
	}
}