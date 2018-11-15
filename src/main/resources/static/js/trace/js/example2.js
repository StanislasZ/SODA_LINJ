(function () {
    require.config({
        paths: {
            // echarts: 'http://echarts.baidu.com/build/dist'
			echarts: "echarts",

		},
    });

    require(
    [
        "echarts",    //eccharts文件夹下的echarts.js
        "echarts/chart/main",   //main.js
		"echarts/chart/map",    //map.js
    ],
    function (echarts, BMapExtension) {
        $('#main').css({
            height:$('body').height(),
            width:$('body').width()
        });

        // 初始化地图
        var BMapExt = new BMapExtension($('#main')[0], BMap, echarts,{
            enableMapClick: false
        });
        var map = BMapExt.getMap();
        var container = BMapExt.getEchartsContainer();

        var startPoint = {
            x: 113.328755, //天河城 
            y: 23.135588
        };

        var point = new BMap.Point(startPoint.x, startPoint.y);
        map.centerAndZoom(point, 17);
        map.enableScrollWheelZoom(true);
        // 地图自定义样式
        map.setMapStyle({
           styleJson: [
          {
                    'featureType': 'land',     //调整土地颜色
                    'elementType': 'geometry',
                    'stylers': {
                              'color': '#081734'
                    }
          },
          {
                    'featureType': 'building',   //调整建筑物颜色
                    'elementType': 'geometry',
                    'stylers': {
                              'color': '#04406F'
                    }
          },
         {
                    'featureType': 'building',   //调整建筑物标签是否可视
                    'elementType': 'labels',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'highway',     //调整高速道路颜色
                    'elementType': 'geometry',
                    'stylers': {
                    'color': '#015B99'
                    }
          },
          {
                    'featureType': 'highway',    //调整高速名字是否可视
                    'elementType': 'labels',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'arterial',   //调整一些干道颜色
                    'elementType': 'geometry',
                    'stylers': {
                    'color':'#003051'
                    }
          },
          {
                    'featureType': 'arterial',
                    'elementType': 'labels',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'green',
                    'elementType': 'geometry',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'water',
                    'elementType': 'geometry',
                    'stylers': {
                              'color': '#044161'
                    }
          },
          {
                    'featureType': 'subway',    //调整地铁颜色
                    'elementType': 'geometry.stroke',
                    'stylers': {
                    'color': '#003051'
                    }
          },
          {
                    'featureType': 'subway',
                    'elementType': 'labels',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'railway',
                    'elementType': 'geometry',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'railway',
                    'elementType': 'labels',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'all',     //调整所有的标签的边缘颜色
                    'elementType': 'labels.text.stroke',
                    'stylers': {
                              'color': '#313131'
                    }
          },
          {
                    'featureType': 'all',     //调整所有标签的填充颜色
                    'elementType': 'labels.text.fill',
                    'stylers': {
                              'color': '#FFFFFF'
                    }
          },
          {
                    'featureType': 'manmade',   
                    'elementType': 'geometry',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'manmade',
                    'elementType': 'labels',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'local',
                    'elementType': 'geometry',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'local',
                    'elementType': 'labels',
                    'stylers': {
                    'visibility': 'off'
                    }
          },
          {
                    'featureType': 'subway',
                    'elementType': 'geometry',
                    'stylers': {
                              'lightness': -65
                    }
          },
          {
                    'featureType': 'railway',
                    'elementType': 'all',
                    'stylers': {
                              'lightness': -40
                    }
          },
          {
                    'featureType': 'boundary',
                    'elementType': 'geometry',
                    'stylers': {
                              'color': '#8b8787',
                              'weight': '1',
                              'lightness': -29
                    }
          }
    ]
        });

option = {
	
    color: ['gold','aqua','lime'],
    title : {
        text: '',
        subtext: '',
        x:'center',
        textStyle : {
            color: '#fff',
			fontSize:20,
			fontWeight:'bold',
        }
    },
    tooltip : {
        show: true,
		trigger:'item',
		hideDelay:4000,
        formatter: function(d) {
			var jw= '经度：'+d.value[0]+'<br/>';
			    jw += '纬度：'+d.value[1];
			return jw       
        }
	},
	color:['gold','red'],
	legend:{
		data:['上班轨迹(甲)','逛街购物轨迹(乙,丙)'],
		x:'left',
		orient:'vertical',
		padding:[30,15,15,30],
		textStyle:{
			fontSize:17,
			color:'rgb(204,204,204)',
		},
		selected:{
			'上班轨迹(甲)':true,
			'逛街购物轨迹(乙,丙)':false,
		},
		selectedMode:'single',
	},
	/*
    toolbox: {
        show : true,
        orient : 'vertical',
        x: 'right',
        y: 'center',
        feature : {
           mark : {show: true},
           dataView : {show: true, readOnly: false},
           restore : {show: true},
           saveAsImage : {show: true}
        }
    },*/
  series : [
{
	  name:'上班轨迹(甲)',
      type:'map',
      mapType: 'none',
      data:[],
      
      markLine : {
      Symbol:['none', 'arrow'],
      symbolSize:['0', '0.1'],
      smooth:true,
      smooth:0,
      effect : {
          show: true,
          scaleSize: 1,
          period: 30,
          color: '#fff',
          shadowBlur: 10
      },
      itemStyle : {
          color: 'red',
          normal: {
              color:function(param){
              return(param.data[0].value.colorValue);
              },
              borderWidth:3,
              lineStyle: {
                  type: 'solid',
                  width: 3,
                  shadowBlur: 10
              },
              label:{show:false,value:'天河城'}
        }
      },

    data : [
	            [{name:'p1'}, {name:'p2',value:{colorValue:'gold'}}],
                [{name:'p2'}, {name:'p3',value:{colorValue:'gold'}}],
                [{name:'p3'}, {name:'p4',value:{colorValue:'gold'}}],
				[{name:'p4'}, {name:'p5',value:{colorValue:'gold'}}],
				[{name:'p5'}, {name:'p6',value:{colorValue:'gold'}}],
				[{name:'p6'}, {name:'p7',value:{colorValue:'gold'}}],
				[{name:'p7'}, {name:'p8',value:{colorValue:'gold'}}],
				[{name:'p8'}, {name:'p9',value:{colorValue:'gold'}}],
				[{name:'p9'}, {name:'p10',value:{colorValue:'gold'}}],
				[{name:'p10'}, {name:'p11',value:{colorValue:'gold'}}],
				[{name:'p11'}, {name:'p12',value:{colorValue:'gold'}}],
				[{name:'p12'}, {name:'p13',value:{colorValue:'gold'}}],
				[{name:'p13'}, {name:'p14',value:{colorValue:'gold'}}],
				[{name:'p14'}, {name:'p15',value:{colorValue:'gold'}}]
		]
		},
	  markPoint:{
			symbol:'image://./image/location.svg',
			symbolSize:function(v){
				return v/5
			},
			effect:{
			    show:true,
                type:'bounce',
                period:3,				
			},
			itemStyle:{
				normal:{
					label:{
						show:false,
					},
				},
				emphasis:{
					label:{
						show:false,
					},
				},
			},
			data:[
			   {name:'p1',value:50,
			       tooltip:{
					   formatter:'时间:8:30am<br/>出发地:中海锦城南苑'
				   },
			   },
			   {name:'p16',value:100,
			       tooltip:{
					   formatter:'天河城<br/>经度:113.328755<br/>纬度:23.137588'  
				   },
			   },
			   {name:'p15',value:50,
			       tooltip:{
					   formatter:'时间:10:00am<br/>目的地:富力盈隆广场'
				   },
			   },
			],
		},
      geoCoord:{
                 'p1':[113.317568,23.135959],
                 'p2':[113.317783,23.135278],
                 'p3':[113.321489,23.135432],
                 'p4':[113.321525,23.135282],
                 'p5':[113.321592,23.13517],
                 'p6':[113.321799,23.135083],
                 'p7':[113.322158,23.135141],
                 'p8':[113.322374,23.135378],
                 'p9':[113.321992,23.135847],
                 'p10':[113.322243,23.13949],
                 'p11':[113.322396,23.1397],
                 'p12':[113.328163,23.13943],
                 'p13':[113.334945,23.139033],
                 'p14':[113.334658,23.133217],
                 'p15':[113.33313,23.13222],
				 'p16':[113.328755, 23.137588]
    }
},
	
	
	
{
	  name:'逛街购物轨迹(乙,丙)',
      type:'map',
      mapType: 'none',
      data:[],
      
      markLine : {
      Symbol:['none', 'arrow'],
      symbolSize:['0', '0.1'],
      smooth:true,
      smooth:20,
      effect : {
          show: true,
          scaleSize: 1,
          period: 30,
          color: '#fff',
          shadowBlur: 10
      },
      itemStyle : {
          color: 'red',
          normal: {
              color:function(param){
              return(param.data[0].value.colorValue);
              },
              borderWidth:3,
              lineStyle: {
                  type: 'solid',
                  width: 3,
                  shadowBlur: 10
              },
              label:{show:false,value:'天河城'}
        }
      },

    data : [
	            [{name:'s1'}, {name:'s2',value:{colorValue:'gold'}}],
                [{name:'s2'}, {name:'s3',value:{colorValue:'gold'}}],
                [{name:'s3'}, {name:'s4',value:{colorValue:'gold'}}],
				[{name:'s4'}, {name:'s10',value:{colorValue:'gold'}}],
				[{name:'s10'}, {name:'s11',value:{colorValue:'gold'}}],
				[{name:'s5'}, {name:'s6',value:{colorValue:'gold'}}],
				[{name:'s6'}, {name:'s7',value:{colorValue:'gold'}}],
				[{name:'s7'}, {name:'s8',value:{colorValue:'gold'}}],
				[{name:'s8'}, {name:'s9',value:{colorValue:'gold'}}],
				[{name:'s9'}, {name:'s10',value:{colorValue:'gold'}}],
				[{name:'s10'}, {name:'s11',value:{colorValue:'gold'}}],
		]
		},
	  markPoint:{
			symbol:'emptyCircle',
			symbolSize:function(v){
				return v/5
			},
			effect:{
			    show:true,
                type:'scale',
                period:10,
                color:'gold',				
			},
			itemStyle:{
				normal:{
					label:{
						show:false,
					},
				},
				emphasis:{
					label:{
						show:false,
					},
				},
			},
			data:[
			   {name:'s1',value:50,
			       tooltip:{
					   formatter:'小乙<br/>时间:8:30am<br/>出发地:东兴小区'
				   },
			   },
			   {name:'s5',value:50,
			       tooltip:{
					   formatter:'小丙<br/>时间:8:10am<br/>出发地:冼村'
				   },
			   },
			   {name:'s10',value:50,
			       //tooltip:{
				   //   formatter:'目的地天河城<br/>经度:113.328755<br/>纬度:23.137588'  
				   //},
			   },
			   {name:'s11',value:100,
			       tooltip:{
					   formatter:'时间:10:00am<br/>目的地:天河城购物广场<br/>小乙与小丙成功会合<br/>开始一天的逛街之旅'
				   },
			   },
			],
		},
      geoCoord:{
                 's1':[113.319283,23.129146],
                 's2':[113.321817,23.129877],
                 's3':[113.32776,23.129612],
                 's4':[113.328069,23.136798],
                 's5':[113.336953,23.131398],
                 's6':[113.336163,23.132711],
                 's7':[113.331384,23.132976],
                 's8':[113.330916,23.132212],
                 's9':[113.330701,23.13681],
                 's10':[113.329749,23.137463],
                 's11':[113.328976,23.137949],
    }
},
	
    ]
};


var myChart = BMapExt.initECharts(container);
window.onresize = myChart.onresize;
BMapExt.setOption(option);
                }
                );
                })();

