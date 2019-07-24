package com.amap.map3d.demo.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.map3d.demo.R;
import com.amap.map3d.demo.util.Constants;

/**
 * AMapV2地图中简单介绍一些Polyline的用法.
 */
public class PolylineActivitybase extends Activity implements
		OnSeekBarChangeListener {
	private static final int WIDTH_MAX = 50;
	private static final int HUE_MAX = 255;
	private static final int ALPHA_MAX = 255;

	private AMap aMap;
	private MapView mapView;
	private Polyline polyline;
	private SeekBar mColorBar;
	private SeekBar mAlphaBar;
	private SeekBar mWidthBar;
	
	/*
	 * 为方便展示多线段纹理颜色等示例事先准备好的经纬度
	 */
	private double Lat_A = 35.909736;
	private double Lon_A = 80.947266;
	
	private double Lat_B = 35.909736;
	private double Lon_B = 89.947266;
	
	private double Lat_C = 31.909736;
	private double Lon_C = 89.947266;
	
	private double Lat_D = 31.909736;
	private double Lon_D = 99.947266;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.polyline_activity);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
	    //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
		mColorBar.setMax(HUE_MAX);
		mColorBar.setProgress(50);

		mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
		mAlphaBar.setMax(ALPHA_MAX);
		mAlphaBar.setProgress(255);

		mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
		mWidthBar.setMax(WIDTH_MAX);
		mWidthBar.setProgress(10);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	private void setUpMap() {
		mColorBar.setOnSeekBarChangeListener(this);
		mAlphaBar.setOnSeekBarChangeListener(this);
		mWidthBar.setOnSeekBarChangeListener(this);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.300299, 106.347656), 4));
		aMap.setMapTextZIndex(2);
		// 绘制一个虚线三角形
		polyline = aMap.addPolyline((new PolylineOptions())
				.add(Constants.SHANGHAI, Constants.BEIJING, Constants.CHENGDU)
				.width(10).setDottedLine(true).geodesic(true)
				.color(Color.argb(255, 1, 1, 1)));
		// 绘制一个乌鲁木齐到哈尔滨的大地曲线
		aMap.addPolyline((new PolylineOptions())
				.add(new LatLng(43.828, 87.621), new LatLng(45.808, 126.55))
				.geodesic(true).color(Color.RED));
		// 绘制一条带有纹理的直线
		aMap.addPolyline((new PolylineOptions())
				.add(new LatLng(29.654, 91.139), new LatLng(22.265, 114.188))
				.setCustomTexture(BitmapDescriptorFactory.defaultMarker()));
		
		
		
		
		addPolylinesWithTexture();
		

		addPolylinesWithColors();
		
		addPolylinesWithGradientColors();
		
		
//		addShownMarker();
//		
//		addPolylineInPlayGround();
	
	}

	
	
	private void addShownMarker() {
		aMap.addMarker(new MarkerOptions().position(new LatLng(116.3478962642899, 39.98098214824056)).title("tip").snippet("步行轨迹展示")).showInfoWindow();
		aMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(116.3478962642899, 39.98098214824056), 17));
			}
		});
		
	}

	private void addPolylinesWithTexture() {
		//四个点
		LatLng A = new LatLng(Lat_A, Lon_A);
		LatLng B = new LatLng(Lat_B, Lon_B);
		LatLng C = new LatLng(Lat_C, Lon_C);
		LatLng D = new LatLng(Lat_D, Lon_D);
		
		//用一个数组来存放纹理
		List<BitmapDescriptor> texTuresList = new ArrayList<BitmapDescriptor>();
		texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_alr));
		texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));
		texTuresList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_alr_night));
		
		//指定某一段用某个纹理，对应texTuresList的index即可, 四个点对应三段颜色
		List<Integer> texIndexList = new ArrayList<Integer>();
		texIndexList.add(0);//对应上面的第0个纹理
		texIndexList.add(2);
		texIndexList.add(1);
		
		
		PolylineOptions options = new PolylineOptions();
		options.width(20);//设置宽度
		
		//加入四个点
		options.add(A,B,C,D);
		
		//加入对应的颜色,使用setCustomTextureList 即表示使用多纹理；
		options.setCustomTextureList(texTuresList);
		
		//设置纹理对应的Index
		options.setCustomTextureIndex(texIndexList);
		
		aMap.addPolyline(options);
	}
	
	/**
	 * 多段颜色（非渐变色）
	 */
	private void addPolylinesWithColors() {
		//四个点
		LatLng A = new LatLng(Lat_A + 1, Lon_A + 1);
		LatLng B = new LatLng(Lat_B + 1, Lon_B + 1);
		LatLng C = new LatLng(Lat_C + 1, Lon_C + 1);
		LatLng D = new LatLng(Lat_D + 1, Lon_D + 1);
		
		//用一个数组来存放颜色，四个点对应三段颜色
		List<Integer> colorList = new ArrayList<Integer>();
		colorList.add(Color.RED);
		colorList.add(Color.YELLOW);
		colorList.add(Color.GREEN);
//		colorList.add(Color.BLACK);
		
		PolylineOptions options = new PolylineOptions();
		options.width(20);//设置宽度
		
		//加入四个点
		options.add(A,B,C,D);
		
		//加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
		options.colorValues(colorList);
		
		aMap.addPolyline(options);
	}
	
	
	/**
	 * 多段颜色（渐变色）
	 */
	private void addPolylinesWithGradientColors() {
		//四个点
		LatLng A = new LatLng(Lat_A + 2, Lon_A + 2);
		LatLng B = new LatLng(Lat_B + 2, Lon_B + 2);
		LatLng C = new LatLng(Lat_C + 2, Lon_C + 2);
		LatLng D = new LatLng(Lat_D + 2, Lon_D + 2);
		
		//用一个数组来存放颜色，渐变色，四个点需要设置四个颜色
		List<Integer> colorList = new ArrayList<Integer>();
		colorList.add(Color.RED);
		colorList.add(Color.YELLOW);
		colorList.add(Color.GREEN);
		colorList.add(Color.BLACK);//如果第四个颜色不添加，那么最后一段将显示上一段的颜色
		
		PolylineOptions options = new PolylineOptions();
		options.width(20);//设置宽度
		
		//加入四个点
		options.add(A,B,C,D);
		
		//加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
		options.colorValues(colorList);
		
		//加上这个属性，表示使用渐变线
		options.useGradient(true);
		
		aMap.addPolyline(options);
	}
	
	/**
	 * 围绕操场的跑道画圆
	 */
	private void addPolylineInPlayGround() {
		List<LatLng> list = readLatLngs();
		List<Integer> colorList = new ArrayList<Integer>();
		int[] colors = new int[]{Color.argb(255, 0, 255, 0),Color.argb(255, 255, 255, 0),Color.argb(255, 255, 0, 0)};
		Random random = new Random();
		for (int i = 0; i < list.size(); i++) {
			colorList.add(colors[random.nextInt(3)]);
		}
		aMap.addPolyline(new PolylineOptions().colorValues(colorList)
				.addAll(list).useGradient(true).width(20));
	}


	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/**
	 * Polyline中对填充颜色，透明度，画笔宽度设置响应事件
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (polyline == null) {
			return;
		}
		if (seekBar == mColorBar) {
			polyline.setColor(Color.argb(progress, 1, 1, 1));
		} else if (seekBar == mAlphaBar) {
			float[] prevHSV = new float[3];
			Color.colorToHSV(polyline.getColor(), prevHSV);
			polyline.setColor(Color.HSVToColor(progress, prevHSV));
		} else if (seekBar == mWidthBar) {
			polyline.setWidth(progress);
		}
	}
	
	
	private List<LatLng> readLatLngs() {
		List<LatLng> points = new ArrayList<LatLng>();
		for (int i = 0; i < coords.length; i += 2) {
			points.add(new LatLng(coords[i+1], coords[i]));
		}
		return points;
	}
	
	private double[] coords = { 116.3499049793749, 39.97617053371078,
			116.34978804908442, 39.97619854213431, 116.349674596623,
			39.97623045687959, 116.34955525200917, 39.97626931100656,
			116.34943728748914, 39.976285626595036, 116.34930864705592,
			39.97628129172198, 116.34918981582413, 39.976260803938594,
			116.34906721558868, 39.97623535890678, 116.34895185151584,
			39.976214717128855, 116.34886935936889, 39.976280148755315,
			116.34873954611332, 39.97628182112874, 116.34860763527448,
			39.97626038855863, 116.3484658907622, 39.976306080391836,
			116.34834585430347, 39.976358252119745, 116.34831166130878,
			39.97645709321835, 116.34827643560175, 39.97655231226543,
			116.34824186261169, 39.976658372925556, 116.34825080406188,
			39.9767570732376, 116.34825631960626, 39.976869087779995,
			116.34822111635201, 39.97698451764595, 116.34822901510276,
			39.977079745909876, 116.34822234337618, 39.97718701787645,
			116.34821627457707, 39.97730766147824, 116.34820593515043,
			39.977417746816776, 116.34821013897107, 39.97753930933358,
			116.34821304891533, 39.977652209132174, 116.34820923399242,
			39.977764016531076, 116.3482045955917, 39.97786190186833,
			116.34822159449203, 39.977958856930286, 116.3482256370537,
			39.97807288885813, 116.3482098441266, 39.978170063673524,
			116.34819564465377, 39.978266951404066, 116.34820541974412,
			39.978380693859116, 116.34819672351216, 39.97848741209275,
			116.34816588867105, 39.978593409607825, 116.34818489339459,
			39.97870216883567, 116.34818473446943, 39.978797222300166,
			116.34817728972234, 39.978893492422685, 116.34816491505472,
			39.978997133775266, 116.34815408537773, 39.97911413849568,
			116.34812908154862, 39.97920553614499, 116.34809495907906,
			39.979308267469264, 116.34805113358091, 39.97939658036473,
			116.3480310509613, 39.979491697188685, 116.3480082124968,
			39.979588529006875, 116.34799530586834, 39.979685789111635,
			116.34798818413954, 39.979801430587926, 116.3479996420353,
			39.97990758587515, 116.34798697544538, 39.980000796262615,
			116.3479912988137, 39.980116318796085, 116.34799204219203,
			39.98021407403913, 116.34798535084123, 39.980325006125696,
			116.34797702460183, 39.98042511477518, 116.34796288754136,
			39.98054129336908, 116.34797509821901, 39.980656820423505,
			116.34793922017285, 39.98074576792626, 116.34792586413015,
			39.98085620772756, 116.3478962642899, 39.98098214824056,
			116.34782449883967, 39.98108306010269, 116.34774758827285,
			39.98115277119176, 116.34761476652932, 39.98115430642997,
			116.34749135408349, 39.98114590845294, 116.34734772765582,
			39.98114337322547, 116.34722082902628, 39.98115066909245,
			116.34708205250223, 39.98114532232906, 116.346963237696,
			39.98112245161927, 116.34681500222743, 39.981136637759604,
			116.34669622104072, 39.981146248090866, 116.34658043260109,
			39.98112495260716, 116.34643721418927, 39.9811107163792,
			116.34631638374302, 39.981085081075676, 116.34614782996252,
			39.98108046779486, 116.3460256053666, 39.981049089345206,
			116.34588814050122, 39.98104839362087, 116.34575119741586,
			39.9810544889668, 116.34562885420186, 39.981040940565734,
			116.34549232235582, 39.98105271658809, 116.34537348820508,
			39.981052294975264, 116.3453513775533, 39.980956549928244,
			116.34535913170319, 39.980847550064965, 116.3453599213023,
			39.980746317519205, 116.34536843740489, 39.980639479164495,
			116.34537495926408, 39.98053422715337, 116.34538035008303,
			39.98044125352027, 116.34538772346092, 39.98033289297528,
			116.34536349731319, 39.98023051372805, 116.34536260276806,
			39.98012594791422, 116.3453837670083, 39.98001929316421,
			116.34540825018034, 39.97992562511559, 116.3454139815165,
			39.97983067204257, 116.3454692718331, 39.97972612100009,
			116.34549808484157, 39.97963443108564, 116.34557491490128,
			39.97954610078795, 116.34567427919617, 39.97949329382014,
			116.34581593978571, 39.979461706341475, 116.34593175289481,
			39.97943040061213, 116.34605918545337, 39.97943104762853,
			116.34618845465063, 39.97945450881118, 116.34630608143885,
			39.979507409386365, 116.34639200057882, 39.9795823518584,
			116.34644168093705, 39.979669367501444, 116.34646446332108,
			39.97976920372734, 116.34644896113649, 39.979869968955356,
			116.34644461326027, 39.97996771476038, 116.34642482084297,
			39.98006233178207, 116.34641802755269, 39.980160053048586,
			116.34639831569697, 39.9802579103449, 116.34637857279766,
			39.98034768726033, 116.34637303457077, 39.980464471612216,
			116.34636777528394, 39.980567295928935, 116.34635887981251,
			39.980685624111615, 116.34632453624687, 39.980789194462794,
			116.34623197117918, 39.98087121581443, 116.34612516918052,
			39.98090910887548, 116.34599333131801, 39.98094493519153,
			116.34586999937214, 39.98095769626178, 116.34573234045392,
			39.980928658857216, 116.34563732280034, 39.9808726896611,
			116.34555035053893, 39.980796824590165, 116.34549289184851,
			39.98070486383829, 116.34544545308052, 39.98059364097801,
			116.3454508725265, 39.980488736921195, 116.34545072109,
			39.980396302972395, 116.34542782701341, 39.980286395923386,
			116.34544464187744, 39.98018226314989, 116.34547855428576,
			39.98007443210545, 116.3454705548606, 39.97997741334671,
			116.34548329911682, 39.979860322467644, 116.3454933004193,
			39.97976939751121, 116.34553948194389, 39.97967720994376,
			116.34561074104593, 39.97960090971443, 116.34571591771297,
			39.97954399337645, 116.345822900572, 39.979506201058975,
			116.34594454541701, 39.97946501577954, 116.34607876512581,
			39.97948337609949, 116.34621888837438, 39.97950718743658,
			116.34630701263039, 39.97958342412466, 116.34639329246936,
			39.97965754723346, 116.34642654753202, 39.97975321273946,
			116.3464144832923, 39.97985685445605, 116.34640902400875,
			39.97996336854432, 116.34638877242116, 39.98007341531857,
			116.3463879846782, 39.98018996847643, 116.3463797800072,
			39.980302217698, 116.3463752632252, 39.98041221367625,
			116.3463525943773, 39.980506415332385, 116.34635082413388,
			39.980620256562915, 116.34633700442113, 39.98071076449841,
			116.34629879052196, 39.9807996970748, 116.34622221105307,
			39.98088358817526, 116.34610546880639, 39.980936353341505,
			116.34597237791989, 39.980970357230895, 116.34585386180052,
			39.980942905643225, 116.34572661643442, 39.980917707763446,
			116.3456257054262, 39.98085954749603, 116.34555030895619,
			39.98078441402001, 116.34549848351193, 39.98069853401107,
			116.3454647245383, 39.98058225645904, 116.34546974388667,
			39.98048395191727, 116.34545378414464, 39.980365057444516,
			116.34544647618465, 39.98026745994793, 116.34545058182921,
			39.98015521315337, 116.34545576120608, 39.980054178806164,
			116.34548411298604, 39.97996075796404, 116.34550864557093,
			39.979861489764765, 116.34551474716052, 39.97976184719169,
			116.3455383987497, 39.979670647665024, 116.3455978408393,
			39.97958792516407, 116.34569662217083, 39.9795214065474,
			116.34582141395762, 39.97947858719891, 116.34595322401007,
			39.97945961170922, 116.34608096875643, 39.979472949783265,
			116.3461845936395, 39.97951728412228, 116.34629949445826,
			39.9795706796597, 116.3463716558699, 39.979668157492966,
			116.34638955748291, 39.97976808467857, 116.34640097458019,
			39.97986662977241, 116.34639935260104, 39.97996328059129,
			116.34639341123477, 39.98006134345006, 116.34639585893093,
			39.98016358204545, 116.3463858592363, 39.98026706758813,
			116.3463748680359, 39.98037629151796, 116.34636892763706,
			39.9804824047001, 116.34635713252801, 39.98057391643512,
			116.34634064842415, 39.98067694992165, 116.34628791410326,
			39.98078019610336, 116.3461855763157, 39.98085355892236,
			116.34604024069753, 39.98090666090162, 116.34590871225686,
			39.98093292731128, 116.34577840085062, 39.9809134940152,
			116.34565797862619, 39.980872388128155, 116.34555178314137,
			39.980791997068486, 116.34550562959198, 39.98070022739962,
			116.34548704566096, 39.98059495857801, 116.34546093448475,
			39.980485395549685, 116.34545577176236, 39.98039171220767,
			116.34545270208339, 39.98028464223807, 116.34544733613131,
			39.98016782760145, 116.34545991200389, 39.98006728706496,
			116.3454739007961, 39.97996492909022, 116.34548079428212,
			39.9798664080389, 116.34549368017511, 39.97976033786034,
			116.34556615646763, 39.97963785809894, 116.34564210569148,
			39.979559286500574, 116.34574557635658, 39.97948419623757,
			116.34589974573612, 39.97946219232232, 116.34607191930978,
			39.979477993160025, 116.34618097553779, 39.97951654737064,
			116.34628577392616, 39.979569344127974, 116.3463544979186,
			39.97966829564768, 116.34638284214738, 39.979764112056834,
			116.34640233694358, 39.979861212086284, 116.34640255981286,
			39.97996424658205, 116.34639368331311, 39.98007363444217,
			116.34637589681806, 39.980180695671955, 116.34637465741476,
			39.98029174777431 };
}
