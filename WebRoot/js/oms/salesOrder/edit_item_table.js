
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
             	   	+'<option value="1"' + (data=='1'?'selected':'') +'>台</option>'
             	   +'<option value="2"' + (data=='2'?'selected':'') +'>座</option>'
             	   +'<option value="3"' + (data=='3'?'selected':'') +'>辆</option>'
             	   +'<option value="4"' + (data=='4'?'selected':'') +'>艘</option>'
             	   +'<option value="5"' + (data=='5'?'selected':'') +'>架</option>'
             	   +'<option value="6"' + (data=='6'?'selected':'') +'>套</option>'
             	   +'<option value="7"' + (data=='7'?'selected':'') +'>个</option>'
             	   +'<option value="8"' + (data=='8'?'selected':'') +'>只</option>'
             	   +'<option value="9"' + (data=='9'?'selected':'') +'>头</option>'
             	   +'<option value="10"' + (data=='10'?'selected':'') +'>张</option>'
             	   +'<option value="11"' + (data=='11'?'selected':'') +'>件</option>'
             	   +'<option value="12"' + (data=='12'?'selected':'') +'>支</option>'
             	   +'<option value="13"' + (data=='13'?'selected':'') +'>枝</option>'
             	   +'<option value="14"' + (data=='14'?'selected':'') +'>根</option>'
             	   +'<option value="15"' + (data=='15'?'selected':'') +'>条</option>'
             	   +'<option value="16"' + (data=='16'?'selected':'') +'>把</option>'
             	   +'<option value="17"' + (data=='17'?'selected':'') +'>块</option>'
             	   +'<option value="18"' + (data=='18'?'selected':'') +'>卷</option>'
             	   +'<option value="19"' + (data=='19'?'selected':'') +'>副</option>'
             	   +'<option value="20"' + (data=='20'?'selected':'') +'>片</option>'
             	   +'<option value="21"' + (data=='21'?'selected':'') +'>组</option>'
             	   +'<option value="22"' + (data=='22'?'selected':'') +'>份</option>'
             	   +'<option value="23"' + (data=='23'?'selected':'') +'>幅</option>'
             	   +'<option value="25"' + (data=='25'?'selected':'') +'>双</option>'
             	   +'<option value="26"' + (data=='26'?'selected':'') +'>对</option>'
             	   +'<option value="27"' + (data=='27'?'selected':'') +'>棵</option>'
             	   +'<option value="28"' + (data=='28'?'selected':'') +'>株</option>'
             	   +'<option value="29"' + (data=='29'?'selected':'') +'>井</option>'
             	   +'<option value="30"' + (data=='30'?'selected':'') +'>米</option>'
             	   +'<option value="31"' + (data=='31'?'selected':'') +'>盘</option>'
             	   +'<option value="32"' + (data=='32'?'selected':'') +'>平方米</option>'
             	   +'<option value="33"' + (data=='33'?'selected':'') +'>立方米</option>'
             	   +'<option value="34"' + (data=='34'?'selected':'') +'>筒</option>'
             	   +'<option value="35"' + (data=='35'?'selected':'') +'>千克</option>'
             	   +'<option value="36"' + (data=='36'?'selected':'') +'>克</option>'
             	   +'<option value="37"' + (data=='37'?'selected':'') +'>盆</option>'
             	   +'<option value="38"' + (data=='38'?'selected':'') +'>万个</option>'
             	   +'<option value="39"' + (data=='39'?'selected':'') +'>具</option>'
             	   +'<option value="40"' + (data=='40'?'selected':'') +'>百副</option>'
             	   +'<option value="41"' + (data=='41'?'selected':'') +'>百支</option>'
             	   +'<option value="42"' + (data=='42'?'selected':'') +'>百把</option>'
             	   +'<option value="43"' + (data=='43'?'selected':'') +'>百个</option>'
             	   +'<option value="44"' + (data=='44'?'selected':'') +'>百片</option>'
             	   +'<option value="45"' + (data=='45'?'selected':'') +'>刀</option>'
             	   +'<option value="46"' + (data=='46'?'selected':'') +'>疋</option>'
             	   +'<option value="47"' + (data=='47'?'selected':'') +'>公担</option>'
             	   +'<option value="48"' + (data=='48'?'selected':'') +'>扇</option>'
             	   +'<option value="49"' + (data=='49'?'selected':'') +'>百枝</option>'
             	   +'<option value="50"' + (data=='50'?'selected':'') +'>千只</option>'
             	   +'<option value="51"' + (data=='51'?'selected':'') +'>千块</option>'
             	   +'<option value="52"' + (data=='52'?'selected':'') +'>千盒</option>'
             	   +'<option value="53"' + (data=='53'?'selected':'') +'>千枝</option>'
             	   +'<option value="54"' + (data=='54'?'selected':'') +'>千个</option>'
             	   +'<option value="55"' + (data=='55'?'selected':'') +'>亿支</option>'
             	   +'<option value="56"' + (data=='56'?'selected':'') +'>亿个</option>'
             	   +'<option value="57"' + (data=='57'?'selected':'') +'>万套</option>'
             	   +'<option value="58"' + (data=='58'?'selected':'') +'>千张</option>'
             	   +'<option value="59"' + (data=='59'?'selected':'') +'>万张</option>'
             	   +'<option value="60"' + (data=='60'?'selected':'') +'>千伏安</option>'
             	   +'<option value="61"' + (data=='61'?'selected':'') +'>千瓦</option>'
             	   +'<option value="62"' + (data=='62'?'selected':'') +'>千瓦时</option>'
             	   +'<option value="63"' + (data=='63'?'selected':'') +'>千升</option>'
             	   +'<option value="67"' + (data=='67'?'selected':'') +'>英尺</option>'
             	   +'<option value="70"' + (data=='70'?'selected':'') +'>吨</option>'
             	   +'<option value="71"' + (data=='71'?'selected':'') +'>长吨</option>'
             	   +'<option value="72"' + (data=='72'?'selected':'') +'>短吨</option>'
             	   +'<option value="73"' + (data=='73'?'selected':'') +'>司马担</option>'
             	   +'<option value="74"' + (data=='74'?'selected':'') +'>司马斤</option>'
             	   +'<option value="75"' + (data=='75'?'selected':'') +'>斤</option>'
             	   +'<option value="76"' + (data=='76'?'selected':'') +'>磅</option>'
             	   +'<option value="77"' + (data=='77'?'selected':'') +'>担</option>'
             	   +'<option value="78"' + (data=='78'?'selected':'') +'>英担</option>'
             	   +'<option value="79"' + (data=='79'?'selected':'') +'>短担</option>'
             	   +'<option value="80"' + (data=='80'?'selected':'') +'>两</option>'
             	   +'<option value="81"' + (data=='81'?'selected':'') +'>市担</option>'
             	   +'<option value="83"' + (data=='83'?'selected':'') +'>盎司</option>'
             	   +'<option value="84"' + (data=='84'?'selected':'') +'>克拉</option>'
             	   +'<option value="85"' + (data=='85'?'selected':'') +'>市尺</option>'
             	   +'<option value="86"' + (data=='86'?'selected':'') +'>码</option>'
             	   +'<option value="88"' + (data=='88'?'selected':'') +'>英寸</option>'
             	   +'<option value="89"' + (data=='89'?'selected':'') +'>寸</option>'
             	   +'<option value="95"' + (data=='95'?'selected':'') +'>升</option>'
             	   +'<option value="96"' + (data=='96'?'selected':'') +'>毫升</option>'
             	   +'<option value="97"' + (data=='97'?'selected':'') +'>英加仑</option>'
             	   +'<option value="98"' + (data=='98'?'selected':'') +'>美加仑</option>'
             	   +'<option value="99"' + (data=='99'?'selected':'') +'>立方英尺</option>'
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
             	   	+'<option value="1"' + (data=='1'?'selected':'') +'>台</option>'
             	   +'<option value="2"' + (data=='2'?'selected':'') +'>座</option>'
             	   +'<option value="3"' + (data=='3'?'selected':'') +'>辆</option>'
             	   +'<option value="4"' + (data=='4'?'selected':'') +'>艘</option>'
             	   +'<option value="5"' + (data=='5'?'selected':'') +'>架</option>'
             	   +'<option value="6"' + (data=='6'?'selected':'') +'>套</option>'
             	   +'<option value="7"' + (data=='7'?'selected':'') +'>个</option>'
             	   +'<option value="8"' + (data=='8'?'selected':'') +'>只</option>'
             	   +'<option value="9"' + (data=='9'?'selected':'') +'>头</option>'
             	   +'<option value="10"' + (data=='10'?'selected':'') +'>张</option>'
             	   +'<option value="11"' + (data=='11'?'selected':'') +'>件</option>'
             	   +'<option value="12"' + (data=='12'?'selected':'') +'>支</option>'
             	   +'<option value="13"' + (data=='13'?'selected':'') +'>枝</option>'
             	   +'<option value="14"' + (data=='14'?'selected':'') +'>根</option>'
             	   +'<option value="15"' + (data=='15'?'selected':'') +'>条</option>'
             	   +'<option value="16"' + (data=='16'?'selected':'') +'>把</option>'
             	   +'<option value="17"' + (data=='17'?'selected':'') +'>块</option>'
             	   +'<option value="18"' + (data=='18'?'selected':'') +'>卷</option>'
             	   +'<option value="19"' + (data=='19'?'selected':'') +'>副</option>'
             	   +'<option value="20"' + (data=='20'?'selected':'') +'>片</option>'
             	   +'<option value="21"' + (data=='21'?'selected':'') +'>组</option>'
             	   +'<option value="22"' + (data=='22'?'selected':'') +'>份</option>'
             	   +'<option value="23"' + (data=='23'?'selected':'') +'>幅</option>'
             	   +'<option value="25"' + (data=='25'?'selected':'') +'>双</option>'
             	   +'<option value="26"' + (data=='26'?'selected':'') +'>对</option>'
             	   +'<option value="27"' + (data=='27'?'selected':'') +'>棵</option>'
             	   +'<option value="28"' + (data=='28'?'selected':'') +'>株</option>'
             	   +'<option value="29"' + (data=='29'?'selected':'') +'>井</option>'
             	   +'<option value="30"' + (data=='30'?'selected':'') +'>米</option>'
             	   +'<option value="31"' + (data=='31'?'selected':'') +'>盘</option>'
             	   +'<option value="32"' + (data=='32'?'selected':'') +'>平方米</option>'
             	   +'<option value="33"' + (data=='33'?'selected':'') +'>立方米</option>'
             	   +'<option value="34"' + (data=='34'?'selected':'') +'>筒</option>'
             	   +'<option value="35"' + (data=='35'?'selected':'') +'>千克</option>'
             	   +'<option value="36"' + (data=='36'?'selected':'') +'>克</option>'
             	   +'<option value="37"' + (data=='37'?'selected':'') +'>盆</option>'
             	   +'<option value="38"' + (data=='38'?'selected':'') +'>万个</option>'
             	   +'<option value="39"' + (data=='39'?'selected':'') +'>具</option>'
             	   +'<option value="40"' + (data=='40'?'selected':'') +'>百副</option>'
             	   +'<option value="41"' + (data=='41'?'selected':'') +'>百支</option>'
             	   +'<option value="42"' + (data=='42'?'selected':'') +'>百把</option>'
             	   +'<option value="43"' + (data=='43'?'selected':'') +'>百个</option>'
             	   +'<option value="44"' + (data=='44'?'selected':'') +'>百片</option>'
             	   +'<option value="45"' + (data=='45'?'selected':'') +'>刀</option>'
             	   +'<option value="46"' + (data=='46'?'selected':'') +'>疋</option>'
             	   +'<option value="47"' + (data=='47'?'selected':'') +'>公担</option>'
             	   +'<option value="48"' + (data=='48'?'selected':'') +'>扇</option>'
             	   +'<option value="49"' + (data=='49'?'selected':'') +'>百枝</option>'
             	   +'<option value="50"' + (data=='50'?'selected':'') +'>千只</option>'
             	   +'<option value="51"' + (data=='51'?'selected':'') +'>千块</option>'
             	   +'<option value="52"' + (data=='52'?'selected':'') +'>千盒</option>'
             	   +'<option value="53"' + (data=='53'?'selected':'') +'>千枝</option>'
             	   +'<option value="54"' + (data=='54'?'selected':'') +'>千个</option>'
             	   +'<option value="55"' + (data=='55'?'selected':'') +'>亿支</option>'
             	   +'<option value="56"' + (data=='56'?'selected':'') +'>亿个</option>'
             	   +'<option value="57"' + (data=='57'?'selected':'') +'>万套</option>'
             	   +'<option value="58"' + (data=='58'?'selected':'') +'>千张</option>'
             	   +'<option value="59"' + (data=='59'?'selected':'') +'>万张</option>'
             	   +'<option value="60"' + (data=='60'?'selected':'') +'>千伏安</option>'
             	   +'<option value="61"' + (data=='61'?'selected':'') +'>千瓦</option>'
             	   +'<option value="62"' + (data=='62'?'selected':'') +'>千瓦时</option>'
             	   +'<option value="63"' + (data=='63'?'selected':'') +'>千升</option>'
             	   +'<option value="67"' + (data=='67'?'selected':'') +'>英尺</option>'
             	   +'<option value="70"' + (data=='70'?'selected':'') +'>吨</option>'
             	   +'<option value="71"' + (data=='71'?'selected':'') +'>长吨</option>'
             	   +'<option value="72"' + (data=='72'?'selected':'') +'>短吨</option>'
             	   +'<option value="73"' + (data=='73'?'selected':'') +'>司马担</option>'
             	   +'<option value="74"' + (data=='74'?'selected':'') +'>司马斤</option>'
             	   +'<option value="75"' + (data=='75'?'selected':'') +'>斤</option>'
             	   +'<option value="76"' + (data=='76'?'selected':'') +'>磅</option>'
             	   +'<option value="77"' + (data=='77'?'selected':'') +'>担</option>'
             	   +'<option value="78"' + (data=='78'?'selected':'') +'>英担</option>'
             	   +'<option value="79"' + (data=='79'?'selected':'') +'>短担</option>'
             	   +'<option value="80"' + (data=='80'?'selected':'') +'>两</option>'
             	   +'<option value="81"' + (data=='81'?'selected':'') +'>市担</option>'
             	   +'<option value="83"' + (data=='83'?'selected':'') +'>盎司</option>'
             	   +'<option value="84"' + (data=='84'?'selected':'') +'>克拉</option>'
             	   +'<option value="85"' + (data=='85'?'selected':'') +'>市尺</option>'
             	   +'<option value="86"' + (data=='86'?'selected':'') +'>码</option>'
             	   +'<option value="88"' + (data=='88'?'selected':'') +'>英寸</option>'
             	   +'<option value="89"' + (data=='89'?'selected':'') +'>寸</option>'
             	   +'<option value="95"' + (data=='95'?'selected':'') +'>升</option>'
             	   +'<option value="96"' + (data=='96'?'selected':'') +'>毫升</option>'
             	   +'<option value="97"' + (data=='97'?'selected':'') +'>英加仑</option>'
             	   +'<option value="98"' + (data=='98'?'selected':'') +'>美加仑</option>'
             	   +'<option value="99"' + (data=='99'?'selected':'') +'>立方英尺</option>'
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