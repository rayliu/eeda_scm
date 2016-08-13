
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
            { "data": "CUS_ITEM_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" name="cus_item_no" value="'+data+'" class="form-control"/>';
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
             	   	   +'<option value="6" '+ (data=='6'?'selected':'') +'>套</option>'
 	                   +'<option value="7" '+ (data=='7'?'selected':'') +'>个</option>'
 	                   +'<option value="8" '+ (data=='8'?'selected':'') +'>只</option>'
 	                   +'<option value="11" '+ (data=='11'?'selected':'') +'>件</option>'
 	                   +'<option value="12" '+ (data=='12'?'selected':'') +'>支</option>'
 	                   +'<option value="14" '+ (data=='14'?'selected':'') +'>根</option>'
 	                   +'<option value="15" '+ (data=='15'?'selected':'') +'>条</option>'
 	                   +'<option value="17" '+ (data=='17'?'selected':'') +'>块</option>'
 	                   +'<option value="18" '+ (data=='18'?'selected':'') +'>卷</option>'
 	                   +'<option value="20" '+ (data=='20'?'selected':'') +'>片</option>'
 	                   +'<option value="35" '+ (data=='35'?'selected':'') +'>千克</option>'
 	                   +'<option value="36" '+ (data=='36'?'selected':'') +'>克</option>'
 	                   +'<option value="70" '+ (data=='70'?'selected':'') +'>吨</option>'
 	                   +'<option value="75" '+ (data=='75'?'selected':'') +'>斤</option>'
 	                   +'<option value="96" '+ (data=='96'?'selected':'') +'>毫升</option>'
 	                   +'<option value="122" '+ (data=='122'?'selected':'') +'>罐</option>'
 	                   +'<option value="125" '+ (data=='125'?'selected':'') +'>包</option>'
 	                   +'<option value="134" '+ (data=='134'?'selected':'') +'>枚</option>'
 	                   +'<option value="136" '+ (data=='136'?'selected':'') +'>袋</option>'
 	                   +'<option value="139" '+ (data=='139'?'selected':'') +'>粒</option>'
 	                   +'<option value="140" '+ (data=='140'?'selected':'') +'>盒</option>'
 	                   +'<option value="142" '+ (data=='142'?'selected':'') +'>瓶</option>'
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
                   return '<input type="text" name="total" value="'+data+'" class="form-control calculate" required/>';
                }
            },
            { "data": "GIFT_FLAG","width": "60px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='0';
                    var str= '<select class="form-control search-control" name="gift_flag">'
              	   	   +'<option value="0" '+ (data=='0'?'selected':'') +'>否</option>'
  	                   +'<option value="1" '+ (data=='1'?'selected':'') +'>是</option>'
  	                   +'</select>';
                    return str;
                }
            },
            { "data": "CURRENCY","width": "60px",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='156';
                    var str= '<select class="form-control search-control" name="currency">'
               	   	   +'<option value="156" '+ (data=='156'?'selected':'') +'>中国</option>'
               	   	   +'<option value="344" '+ (data=='344'?'selected':'') +'>港币</option>'
               	   	   +'<option value="392" '+ (data=='392'?'selected':'') +'>日本元</option>'
               	   	   +'<option value="446" '+ (data=='446'?'selected':'') +'>澳门元</option>'
               	   	   +'<option value="608" '+ (data=='608'?'selected':'') +'>菲律宾比索</option>'
               	   	   +'<option value="702" '+ (data=='702'?'selected':'') +'>新加坡元</option>'
               	   	   +'<option value="410" '+ (data=='410'?'selected':'') +'>韩国圆</option>'
            	   	   +'<option value="764" '+ (data=='764'?'selected':'') +'>泰国铢</option>'
            	   	   +'<option value="978" '+ (data=='978'?'selected':'') +'>欧元</option>'
            	   	   +'<option value="208" '+ (data=='208'?'selected':'') +'>丹麦克朗</option>'
            	   	   +'<option value="826" '+ (data=='826'?'selected':'') +'>英镑</option>'
            	   	   +'<option value="840" '+ (data=='840'?'selected':'') +'>美元</option>'
   	                   +'</select>';
                    return str;
                }
            }
        ]
    });
    
    $('#cargo_table').on('input','.calculate',function(){
    	var row = $(this).parent().parent();
    	var sty = $(row.find('.calculate')[0]).val()==''?'0':$(row.find('.calculate')[0]).val();
    	var price = $(row.find('.calculate')[1]).val()==''?'0':$(row.find('.calculate')[1]).val();
    	var total = $(row.find('.calculate')[2]).val(parseFloat(sty)*parseFloat(price));
    })

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