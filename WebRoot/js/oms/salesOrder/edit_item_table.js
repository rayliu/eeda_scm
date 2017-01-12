
$(document).ready(function() {

    var deletedTableIds=[];

    //删除一行
    $("#cargo_table").on('click', '.delete', function(e){
        e.preventDefault();
        var tr = $(this).parent().parent();
        deletedTableIds.push(tr.attr('id'))
        
        cargoTable.row(tr).remove().draw();
    }); 

    salesOrder.buildCargoDetail=function(){
    	var item_table_rows = $("#cargo_table tr");
        var items_array=[];
        for(var index=0; index<item_table_rows.length; index++){
            if(index==0)
                continue;

            var row = item_table_rows[index];
            var empty = $(row).find('.dataTables_empty').text();
            if(empty)
            	continue;
            
            var id = $(row).attr('id');
            if(!id){
                id='';
            }
            
            var item={}
            item.id = id;
            for(var i = 1; i < row.childNodes.length; i++){
            	var name = $(row.childNodes[i]).find('input,select').attr('name');
            	var value = $(row.childNodes[i]).find('input,select').val();
            	if(name){
            		item[name] = value;
            	}
            }
            item.action = id.length > 0?'UPDATE':'CREATE';
            items_array.push(item);
        }

        //add deleted items
        for(var index=0; index<deletedTableIds.length; index++){
            var id = deletedTableIds[index];
            var item={
                id: id,
                action: 'DELETE'
            };
            items_array.push(item);
        }
        deletedTableIds = [];
        return items_array;
    };

   

    //------------事件处理
    var cargoTable = $('#cargo_table').DataTable({
    	 "processing": true,
         "searching": false,
         "paging": false,
         "info": false,
         "autoWidth": false,
         "serverSide": false,
         "scrollX":  true,
         "responsive": true,
         "scrollY":  true, 
         "scrollCollapse":  true,
        "language": {
            "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
        },
        "createdRow": function ( row, data, index ) {
            $(row).attr('id', data.ID);
        },
        "columns": [
            { "width": "30px",
                "render": function ( data, type, full, meta ) {
                	return '<button type="button" class="delete btn btn-default btn-xs">删除</button> ';
                }
            },
            { "data": "BAR_CODE", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="bar_code" value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "ITEM_NAME", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="item_name" value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "ITEM_DESC",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="item_desc" value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "ITEM_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="item_no" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "QTY" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="qty" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "UNIT","width": "60px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='122';
                    var str= '<select class="form-control search-control" name="unit">'
             	   	   +'<option></option>'
             	   +'<option value="001"' + (data=='001'?'selected':'') +'>台</option>'
             	   +'<option value="002"' + (data=='002'?'selected':'') +'>座</option>'
             	   +'<option value="003"' + (data=='003'?'selected':'') +'>辆</option>'
             	   +'<option value="004"' + (data=='004'?'selected':'') +'>艘</option>'
             	   +'<option value="005"' + (data=='005'?'selected':'') +'>架</option>'
             	   +'<option value="006"' + (data=='006'?'selected':'') +'>套</option>'
             	   +'<option value="007"' + (data=='007'?'selected':'') +'>个</option>'
             	   +'<option value="008"' + (data=='008'?'selected':'') +'>只</option>'
             	   +'<option value="009"' + (data=='009'?'selected':'') +'>头</option>'
             	   +'<option value="010"' + (data=='010'?'selected':'') +'>张</option>'
             	   +'<option value="011"' + (data=='011'?'selected':'') +'>件</option>'
             	   +'<option value="012"' + (data=='012'?'selected':'') +'>支</option>'
             	   +'<option value="013"' + (data=='013'?'selected':'') +'>枝</option>'
             	   +'<option value="014"' + (data=='014'?'selected':'') +'>根</option>'
             	   +'<option value="015"' + (data=='015'?'selected':'') +'>条</option>'
             	   +'<option value="016"' + (data=='016'?'selected':'') +'>把</option>'
             	   +'<option value="017"' + (data=='017'?'selected':'') +'>块</option>'
             	   +'<option value="018"' + (data=='018'?'selected':'') +'>卷</option>'
             	   +'<option value="019"' + (data=='019'?'selected':'') +'>副</option>'
            	   +'<option value="020"' + (data=='020'?'selected':'') +'>片</option>'
            	   +'<option value="021"' + (data=='021'?'selected':'') +'>组</option>'
            	   +'<option value="022"' + (data=='022'?'selected':'') +'>份</option>'
            	   +'<option value="023"' + (data=='023'?'selected':'') +'>幅</option>'
            	   +'<option value="025"' + (data=='025'?'selected':'') +'>双</option>'
            	   +'<option value="026"' + (data=='026'?'selected':'') +'>对</option>'
            	   +'<option value="027"' + (data=='027'?'selected':'') +'>棵</option>'
            	   +'<option value="028"' + (data=='028'?'selected':'') +'>株</option>'
            	   +'<option value="029"' + (data=='029'?'selected':'') +'>井</option>'
            	   +'<option value="030"' + (data=='030'?'selected':'') +'>米</option>'
            	   +'<option value="031"' + (data=='031'?'selected':'') +'>盘</option>'
            	   +'<option value="032"' + (data=='032'?'selected':'') +'>平方米</option>'
            	   +'<option value="033"' + (data=='033'?'selected':'') +'>立方米</option>'
            	   +'<option value="034"' + (data=='034'?'selected':'') +'>筒</option>'
            	   +'<option value="035"' + (data=='035'?'selected':'') +'>千克</option>'
            	   +'<option value="036"' + (data=='036'?'selected':'') +'>克</option>'
            	   +'<option value="037"' + (data=='037'?'selected':'') +'>盆</option>'
            	   +'<option value="038"' + (data=='038'?'selected':'') +'>万个</option>'
            	   +'<option value="039"' + (data=='039'?'selected':'') +'>具</option>'
            	   +'<option value="040"' + (data=='040'?'selected':'') +'>百副</option>'
            	   +'<option value="041"' + (data=='041'?'selected':'') +'>百支</option>'
            	   +'<option value="042"' + (data=='042'?'selected':'') +'>百把</option>'
            	   +'<option value="043"' + (data=='043'?'selected':'') +'>百个</option>'
            	   +'<option value="044"' + (data=='044'?'selected':'') +'>百片</option>'
            	   +'<option value="045"' + (data=='045'?'selected':'') +'>刀</option>'
            	   +'<option value="046"' + (data=='046'?'selected':'') +'>疋</option>'
            	   +'<option value="047"' + (data=='047'?'selected':'') +'>公担</option>'
            	   +'<option value="048"' + (data=='048'?'selected':'') +'>扇</option>'
            	   +'<option value="049"' + (data=='049'?'selected':'') +'>百枝</option>'
            	   +'<option value="050"' + (data=='050'?'selected':'') +'>千只</option>'
            	   +'<option value="051"' + (data=='051'?'selected':'') +'>千块</option>'
            	   +'<option value="052"' + (data=='052'?'selected':'') +'>千盒</option>'
            	   +'<option value="053"' + (data=='053'?'selected':'') +'>千枝</option>'
            	   +'<option value="054"' + (data=='054'?'selected':'') +'>千个</option>'
            	   +'<option value="055"' + (data=='055'?'selected':'') +'>亿支</option>'
            	   +'<option value="056"' + (data=='056'?'selected':'') +'>亿个</option>'
            	   +'<option value="057"' + (data=='057'?'selected':'') +'>万套</option>'
            	   +'<option value="058"' + (data=='058'?'selected':'') +'>千张</option>'
            	   +'<option value="059"' + (data=='059'?'selected':'') +'>万张</option>'
            	   +'<option value="060"' + (data=='060'?'selected':'') +'>千伏安</option>'
            	   +'<option value="061"' + (data=='061'?'selected':'') +'>千瓦</option>'
            	   +'<option value="062"' + (data=='062'?'selected':'') +'>千瓦时</option>'
            	   +'<option value="063"' + (data=='063'?'selected':'') +'>千升</option>'
            	   +'<option value="067"' + (data=='067'?'selected':'') +'>英尺</option>'
            	   +'<option value="070"' + (data=='070'?'selected':'') +'>吨</option>'
            	   +'<option value="071"' + (data=='071'?'selected':'') +'>长吨</option>'
            	   +'<option value="072"' + (data=='072'?'selected':'') +'>短吨</option>'
            	   +'<option value="073"' + (data=='073'?'selected':'') +'>司马担</option>'
            	   +'<option value="074"' + (data=='074'?'selected':'') +'>司马斤</option>'
            	   +'<option value="075"' + (data=='075'?'selected':'') +'>斤</option>'
            	   +'<option value="076"' + (data=='076'?'selected':'') +'>磅</option>'
            	   +'<option value="077"' + (data=='077'?'selected':'') +'>担</option>'
            	   +'<option value="078"' + (data=='078'?'selected':'') +'>英担</option>'
            	   +'<option value="079"' + (data=='079'?'selected':'') +'>短担</option>'
            	   +'<option value="080"' + (data=='080'?'selected':'') +'>两</option>'
            	   +'<option value="081"' + (data=='081'?'selected':'') +'>市担</option>'
            	   +'<option value="083"' + (data=='083'?'selected':'') +'>盎司</option>'
            	   +'<option value="084"' + (data=='084'?'selected':'') +'>克拉</option>'
            	   +'<option value="085"' + (data=='085'?'selected':'') +'>市尺</option>'
            	   +'<option value="086"' + (data=='086'?'selected':'') +'>码</option>'
            	   +'<option value="088"' + (data=='088'?'selected':'') +'>英寸</option>'
            	   +'<option value="089"' + (data=='089'?'selected':'') +'>寸</option>'
            	   +'<option value="095"' + (data=='095'?'selected':'') +'>升</option>'
            	   +'<option value="096"' + (data=='096'?'selected':'') +'>毫升</option>'
            	   +'<option value="097"' + (data=='097'?'selected':'') +'>英加仑</option>'
            	   +'<option value="098"' + (data=='098'?'selected':'') +'>美加仑</option>'
            	   +'<option value="099"' + (data=='099'?'selected':'') +'>立方英尺</option>'
             	   +'<option value="101"' + (data=='101'?'selected':'') +'>立方尺</option>'
             	   +'<option value="110"' + (data=='110'?'selected':'') +'>平方码</option>'
             	   +'<option value="111"' + (data=='111'?'selected':'') +'>平方英尺</option>'
             	   +'<option value="112"' + (data=='112'?'selected':'') +'>平方尺</option>'
             	   +'<option value="115"' + (data=='115'?'selected':'') +'>英制马力</option>'
             	   +'<option value="116"' + (data=='116'?'selected':'') +'>公制马力</option>'
             	   +'<option value="118"' + (data=='118'?'selected':'') +'>令</option>'
             	   +'<option value="120"' + (data=='120'?'selected':'') +'>箱</option>'
             	   +'<option value="121"' + (data=='121'?'selected':'') +'>批</option>'
             	   +'<option value="122"' + (data=='122'?'selected':'') +'>罐</option>'
             	   +'<option value="123"' + (data=='123'?'selected':'') +'>桶</option>'
             	   +'<option value="124"' + (data=='124'?'selected':'') +'>扎</option>'
             	   +'<option value="125"' + (data=='125'?'selected':'') +'>包</option>'
             	   +'<option value="126"' + (data=='126'?'selected':'') +'>箩</option>'
             	   +'<option value="127"' + (data=='127'?'selected':'') +'>打</option>'
             	   +'<option value="128"' + (data=='128'?'selected':'') +'>筐</option>'
             	   +'<option value="129"' + (data=='129'?'selected':'') +'>罗</option>'
             	   +'<option value="130"' + (data=='130'?'selected':'') +'>匹</option>'
             	   +'<option value="131"' + (data=='131'?'selected':'') +'>册</option>'
             	   +'<option value="132"' + (data=='132'?'selected':'') +'>本</option>'
             	   +'<option value="133"' + (data=='133'?'selected':'') +'>发</option>'
             	   +'<option value="134"' + (data=='134'?'selected':'') +'>枚</option>'
             	   +'<option value="135"' + (data=='135'?'selected':'') +'>捆</option>'
             	   +'<option value="136"' + (data=='136'?'selected':'') +'>袋</option>'
             	   +'<option value="139"' + (data=='139'?'selected':'') +'>粒</option>'
             	   +'<option value="140"' + (data=='140'?'selected':'') +'>盒</option>'
             	   +'<option value="141"' + (data=='141'?'selected':'') +'>合</option>'
             	   +'<option value="142"' + (data=='142'?'selected':'') +'>瓶</option>'
             	   +'<option value="143"' + (data=='143'?'selected':'') +'>千支</option>'
             	   +'<option value="144"' + (data=='144'?'selected':'') +'>万双</option>'
             	   +'<option value="145"' + (data=='145'?'selected':'') +'>万粒</option>'
             	   +'<option value="146"' + (data=='146'?'selected':'') +'>千粒</option>'
             	   +'<option value="147"' + (data=='147'?'selected':'') +'>千米</option>'
             	   +'<option value="148"' + (data=='148'?'selected':'') +'>千英尺</option>'
             	   +'<option value="163"' + (data=='163'?'selected':'') +'>部</option>'
             	   +'<option value="164"' + (data=='164'?'selected':'') +'>亿株</option>'

	                   +'</select>';
                     return str;
                }
            },
            { "data": "QTY1" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="qty1" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "UNIT1","width": "60px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='122';
                    var str= '<select class="form-control search-control" name="unit1">'
             	   	   +'<option></option>'
             	   	+'<option value="001"' + (data=='001'?'selected':'') +'>台</option>'
              	   +'<option value="002"' + (data=='002'?'selected':'') +'>座</option>'
              	   +'<option value="003"' + (data=='003'?'selected':'') +'>辆</option>'
              	   +'<option value="004"' + (data=='004'?'selected':'') +'>艘</option>'
              	   +'<option value="005"' + (data=='005'?'selected':'') +'>架</option>'
              	   +'<option value="006"' + (data=='006'?'selected':'') +'>套</option>'
              	   +'<option value="007"' + (data=='007'?'selected':'') +'>个</option>'
              	   +'<option value="008"' + (data=='008'?'selected':'') +'>只</option>'
              	   +'<option value="009"' + (data=='009'?'selected':'') +'>头</option>'
              	   +'<option value="010"' + (data=='010'?'selected':'') +'>张</option>'
              	   +'<option value="011"' + (data=='011'?'selected':'') +'>件</option>'
              	   +'<option value="012"' + (data=='012'?'selected':'') +'>支</option>'
              	   +'<option value="013"' + (data=='013'?'selected':'') +'>枝</option>'
              	   +'<option value="014"' + (data=='014'?'selected':'') +'>根</option>'
              	   +'<option value="015"' + (data=='015'?'selected':'') +'>条</option>'
              	   +'<option value="016"' + (data=='016'?'selected':'') +'>把</option>'
              	   +'<option value="017"' + (data=='017'?'selected':'') +'>块</option>'
              	   +'<option value="018"' + (data=='018'?'selected':'') +'>卷</option>'
              	   +'<option value="019"' + (data=='019'?'selected':'') +'>副</option>'
             	   +'<option value="020"' + (data=='020'?'selected':'') +'>片</option>'
             	   +'<option value="021"' + (data=='021'?'selected':'') +'>组</option>'
             	   +'<option value="022"' + (data=='022'?'selected':'') +'>份</option>'
             	   +'<option value="023"' + (data=='023'?'selected':'') +'>幅</option>'
             	   +'<option value="025"' + (data=='025'?'selected':'') +'>双</option>'
             	   +'<option value="026"' + (data=='026'?'selected':'') +'>对</option>'
             	   +'<option value="027"' + (data=='027'?'selected':'') +'>棵</option>'
             	   +'<option value="028"' + (data=='028'?'selected':'') +'>株</option>'
             	   +'<option value="029"' + (data=='029'?'selected':'') +'>井</option>'
             	   +'<option value="030"' + (data=='030'?'selected':'') +'>米</option>'
             	   +'<option value="031"' + (data=='031'?'selected':'') +'>盘</option>'
             	   +'<option value="032"' + (data=='032'?'selected':'') +'>平方米</option>'
             	   +'<option value="033"' + (data=='033'?'selected':'') +'>立方米</option>'
             	   +'<option value="034"' + (data=='034'?'selected':'') +'>筒</option>'
             	   +'<option value="035"' + (data=='035'?'selected':'') +'>千克</option>'
             	   +'<option value="036"' + (data=='036'?'selected':'') +'>克</option>'
             	   +'<option value="037"' + (data=='037'?'selected':'') +'>盆</option>'
             	   +'<option value="038"' + (data=='038'?'selected':'') +'>万个</option>'
             	   +'<option value="039"' + (data=='039'?'selected':'') +'>具</option>'
             	   +'<option value="040"' + (data=='040'?'selected':'') +'>百副</option>'
             	   +'<option value="041"' + (data=='041'?'selected':'') +'>百支</option>'
             	   +'<option value="042"' + (data=='042'?'selected':'') +'>百把</option>'
             	   +'<option value="043"' + (data=='043'?'selected':'') +'>百个</option>'
             	   +'<option value="044"' + (data=='044'?'selected':'') +'>百片</option>'
             	   +'<option value="045"' + (data=='045'?'selected':'') +'>刀</option>'
             	   +'<option value="046"' + (data=='046'?'selected':'') +'>疋</option>'
             	   +'<option value="047"' + (data=='047'?'selected':'') +'>公担</option>'
             	   +'<option value="048"' + (data=='048'?'selected':'') +'>扇</option>'
             	   +'<option value="049"' + (data=='049'?'selected':'') +'>百枝</option>'
             	   +'<option value="050"' + (data=='050'?'selected':'') +'>千只</option>'
             	   +'<option value="051"' + (data=='051'?'selected':'') +'>千块</option>'
             	   +'<option value="052"' + (data=='052'?'selected':'') +'>千盒</option>'
             	   +'<option value="053"' + (data=='053'?'selected':'') +'>千枝</option>'
             	   +'<option value="054"' + (data=='054'?'selected':'') +'>千个</option>'
             	   +'<option value="055"' + (data=='055'?'selected':'') +'>亿支</option>'
             	   +'<option value="056"' + (data=='056'?'selected':'') +'>亿个</option>'
             	   +'<option value="057"' + (data=='057'?'selected':'') +'>万套</option>'
             	   +'<option value="058"' + (data=='058'?'selected':'') +'>千张</option>'
             	   +'<option value="059"' + (data=='059'?'selected':'') +'>万张</option>'
             	   +'<option value="060"' + (data=='060'?'selected':'') +'>千伏安</option>'
             	   +'<option value="061"' + (data=='061'?'selected':'') +'>千瓦</option>'
             	   +'<option value="062"' + (data=='062'?'selected':'') +'>千瓦时</option>'
             	   +'<option value="063"' + (data=='063'?'selected':'') +'>千升</option>'
             	   +'<option value="067"' + (data=='067'?'selected':'') +'>英尺</option>'
             	   +'<option value="070"' + (data=='070'?'selected':'') +'>吨</option>'
             	   +'<option value="071"' + (data=='071'?'selected':'') +'>长吨</option>'
             	   +'<option value="072"' + (data=='072'?'selected':'') +'>短吨</option>'
             	   +'<option value="073"' + (data=='073'?'selected':'') +'>司马担</option>'
             	   +'<option value="074"' + (data=='074'?'selected':'') +'>司马斤</option>'
             	   +'<option value="075"' + (data=='075'?'selected':'') +'>斤</option>'
             	   +'<option value="076"' + (data=='076'?'selected':'') +'>磅</option>'
             	   +'<option value="077"' + (data=='077'?'selected':'') +'>担</option>'
             	   +'<option value="078"' + (data=='078'?'selected':'') +'>英担</option>'
             	   +'<option value="079"' + (data=='079'?'selected':'') +'>短担</option>'
             	   +'<option value="080"' + (data=='080'?'selected':'') +'>两</option>'
             	   +'<option value="081"' + (data=='081'?'selected':'') +'>市担</option>'
             	   +'<option value="083"' + (data=='083'?'selected':'') +'>盎司</option>'
             	   +'<option value="084"' + (data=='084'?'selected':'') +'>克拉</option>'
             	   +'<option value="085"' + (data=='085'?'selected':'') +'>市尺</option>'
             	   +'<option value="086"' + (data=='086'?'selected':'') +'>码</option>'
             	   +'<option value="088"' + (data=='088'?'selected':'') +'>英寸</option>'
             	   +'<option value="089"' + (data=='089'?'selected':'') +'>寸</option>'
             	   +'<option value="095"' + (data=='095'?'selected':'') +'>升</option>'
             	   +'<option value="096"' + (data=='096'?'selected':'') +'>毫升</option>'
             	   +'<option value="097"' + (data=='097'?'selected':'') +'>英加仑</option>'
             	   +'<option value="098"' + (data=='098'?'selected':'') +'>美加仑</option>'
             	   +'<option value="099"' + (data=='099'?'selected':'') +'>立方英尺</option>'
              	   +'<option value="101"' + (data=='101'?'selected':'') +'>立方尺</option>'
              	   +'<option value="110"' + (data=='110'?'selected':'') +'>平方码</option>'
              	   +'<option value="111"' + (data=='111'?'selected':'') +'>平方英尺</option>'
              	   +'<option value="112"' + (data=='112'?'selected':'') +'>平方尺</option>'
              	   +'<option value="115"' + (data=='115'?'selected':'') +'>英制马力</option>'
              	   +'<option value="116"' + (data=='116'?'selected':'') +'>公制马力</option>'
              	   +'<option value="118"' + (data=='118'?'selected':'') +'>令</option>'
              	   +'<option value="120"' + (data=='120'?'selected':'') +'>箱</option>'
              	   +'<option value="121"' + (data=='121'?'selected':'') +'>批</option>'
              	   +'<option value="122"' + (data=='122'?'selected':'') +'>罐</option>'
              	   +'<option value="123"' + (data=='123'?'selected':'') +'>桶</option>'
              	   +'<option value="124"' + (data=='124'?'selected':'') +'>扎</option>'
              	   +'<option value="125"' + (data=='125'?'selected':'') +'>包</option>'
              	   +'<option value="126"' + (data=='126'?'selected':'') +'>箩</option>'
              	   +'<option value="127"' + (data=='127'?'selected':'') +'>打</option>'
              	   +'<option value="128"' + (data=='128'?'selected':'') +'>筐</option>'
              	   +'<option value="129"' + (data=='129'?'selected':'') +'>罗</option>'
              	   +'<option value="130"' + (data=='130'?'selected':'') +'>匹</option>'
              	   +'<option value="131"' + (data=='131'?'selected':'') +'>册</option>'
              	   +'<option value="132"' + (data=='132'?'selected':'') +'>本</option>'
              	   +'<option value="133"' + (data=='133'?'selected':'') +'>发</option>'
              	   +'<option value="134"' + (data=='134'?'selected':'') +'>枚</option>'
              	   +'<option value="135"' + (data=='135'?'selected':'') +'>捆</option>'
              	   +'<option value="136"' + (data=='136'?'selected':'') +'>袋</option>'
              	   +'<option value="139"' + (data=='139'?'selected':'') +'>粒</option>'
              	   +'<option value="140"' + (data=='140'?'selected':'') +'>盒</option>'
              	   +'<option value="141"' + (data=='141'?'selected':'') +'>合</option>'
              	   +'<option value="142"' + (data=='142'?'selected':'') +'>瓶</option>'
              	   +'<option value="143"' + (data=='143'?'selected':'') +'>千支</option>'
              	   +'<option value="144"' + (data=='144'?'selected':'') +'>万双</option>'
              	   +'<option value="145"' + (data=='145'?'selected':'') +'>万粒</option>'
              	   +'<option value="146"' + (data=='146'?'selected':'') +'>千粒</option>'
              	   +'<option value="147"' + (data=='147'?'selected':'') +'>千米</option>'
              	   +'<option value="148"' + (data=='148'?'selected':'') +'>千英尺</option>'
              	   +'<option value="163"' + (data=='163'?'selected':'') +'>部</option>'
              	   +'<option value="164"' + (data=='164'?'selected':'') +'>亿株</option>'
	                   +'</select>';
                     return str;
                }
            },
            { "data": "PRICE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="price" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "TOTAL",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='0';
                   return '<input disabled type="text" name="total" value="'+data+'" class="form-control calculate"/>';
                }
            },
            { "data": "TAX_RATE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="tax_rate" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "AFTER_TAX_TOTAL",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='0';
                   return '<input type="text" name="after_tax_total" value="'+data+'" class="form-control calculate" required/>';
                }
            },
           
            { "data": "CURRENCY","width": "60px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='142';
                    var str= '<select class="form-control search-control" name="currency">'
               	   	   +'<option value="142" '+ (data=='142'?'selected':'') +'>中国</option>'
               	   	   +'<option value="110" '+ (data=='110'?'selected':'') +'>港币</option>'
               	   	   +'<option value="116" '+ (data=='116'?'selected':'') +'>日本元</option>'
               	   	   +'<option value="121" '+ (data=='121'?'selected':'') +'>澳门元</option>'
            	   	   +'<option value="300" '+ (data=='300'?'selected':'') +'>欧元</option>'
            	   	   +'<option value="303" '+ (data=='303'?'selected':'') +'>英镑</option>'
            	   	   +'<option value="502" '+ (data=='502'?'selected':'') +'>美元</option>'
            	   	   +'<option value="601" '+ (data=='601'?'selected':'') +'>澳大利亚元</option>'
            	   	   +'<option value="609" '+ (data=='609'?'selected':'') +'>新西兰元</option>'
   	                   +'</select>';
                    return str;
                }
            },
            { "data": "COUNTRY",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='142';
                   return '<input type="text" name="country" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "GCODE",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="gcode" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "G_MODEL",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="g_model" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CIQ_GNO",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="CIQ_GNO" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "CIQ_GMODEL",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" name="CIQ_GMODEL" value="'+data+'" class="form-control" required/>';
                }
            },
            { "data": "BRAND",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='无';
                   return '<input type="text" name="BRAND" value="'+data+'" class="form-control" required/>';
                }
            }
        ]
    });
    
    
    var decimal =  function(num)  
    {  
        var vv = Math.pow(10,2);  
        return Math.round(num*vv)/vv;  
    }
    
    $('#cargo_table').on('input','.calculate',function(){
    	var row = $(this).parent().parent();
    	var sty = $(row.find('.calculate')[0]).val()==''?'0':$(row.find('.calculate')[0]).val();
    	var price = $(row.find('.calculate')[1]).val()==''?'0':$(row.find('.calculate')[1]).val();
    	var tax_rate = $(row.find('.calculate')[3]).val()==''?'0':$(row.find('.calculate')[3]).val();
    	var total = parseFloat(sty)*parseFloat(price);
    	$(row.find('.calculate')[2]).val(decimal(total));
    	$(row.find('.calculate')[4]).val(decimal(total*(parseFloat(tax_rate)+1)));
    	calAmount();
    })
    
    
    var calAmount= function(){
    	var total_amount = 0;
    	var after_total_amount = 0;
    	$('#cargo_table tr').each(function(){
    		var after_tax_total = $(this).find('[name=after_tax_total]').val();
    		var total = $(this).find('[name=total]').val();
    		if(total)
    			total_amount += parseFloat(total);
    		if(after_tax_total)
    			after_total_amount += parseFloat(after_tax_total);
    	})
    	$('#goods_value').val(decimal(total_amount));
    	$('#actural_paid').val(decimal(after_total_amount));
    }

    $('#add_cargo').on('click', function(){
        var item={};
        cargoTable.row.add(item).draw(false);
    });
    
  //刷新明细表
    salesOrder.refleshItemTable = function(order_id){
    	var url = "/salesOrder/tableList?order_id="+order_id
        +"&table_type=item";
    	cargoTable.ajax.url(url).load();
    }

    
} );