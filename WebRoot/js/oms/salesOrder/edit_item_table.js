
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
            { "data": "CUS_ITEM_NO", 'visible':false,
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
             	   	   +'<option value="006" '+ (data=='006'?'selected':'') +'>套</option>'
 	                   +'<option value="007" '+ (data=='007'?'selected':'') +'>个</option>'
 	                   +'<option value="008" '+ (data=='008'?'selected':'') +'>只</option>'
 	                   +'<option value="011" '+ (data=='011'?'selected':'') +'>件</option>'
 	                   +'<option value="012" '+ (data=='012'?'selected':'') +'>支</option>'
 	                   +'<option value="014" '+ (data=='014'?'selected':'') +'>根</option>'
 	                   +'<option value="015" '+ (data=='015'?'selected':'') +'>条</option>'
 	                   +'<option value="017" '+ (data=='017'?'selected':'') +'>块</option>'
 	                   +'<option value="018" '+ (data=='018'?'selected':'') +'>卷</option>'
 	                   +'<option value="020" '+ (data=='020'?'selected':'') +'>片</option>'
 	                   +'<option value="034" '+ (data=='034'?'selected':'') +'>千克</option>'
 	                   +'<option value="035" '+ (data=='035'?'selected':'') +'>克</option>'
 	                   +'<option value="064" '+ (data=='064'?'selected':'') +'>吨</option>'
 	                   +'<option value="069" '+ (data=='069'?'selected':'') +'>斤</option>'
 	                   +'<option value="083" '+ (data=='083'?'selected':'') +'>毫升</option>'
 	                   +'<option value="094" '+ (data=='094'?'selected':'') +'>箱</option>'
 	                   +'<option value="096" '+ (data=='096'?'selected':'') +'>罐</option>'
 	                   +'<option value="099" '+ (data=='099'?'selected':'') +'>包</option>'
 	                   +'<option value="108" '+ (data=='108'?'selected':'') +'>枚</option>'
 	                   +'<option value="110" '+ (data=='110'?'selected':'') +'>袋</option>'
 	                   +'<option value="111" '+ (data=='111'?'selected':'') +'>粒</option>'
 	                   +'<option value="112" '+ (data=='112'?'selected':'') +'>盒</option>'
 	                   +'<option value="114" '+ (data=='114'?'selected':'') +'>瓶</option>'
 	                   
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
    	if(tax_rate != 0)
    		$(row.find('.calculate')[4]).val(decimal(total*(parseFloat(tax_rate)+1)));
    	calAmount();
    })
    
    
    var calAmount= function(){
    	var total_amount = 0;
    	$('#cargo_table tr').each(function(){
    		var total = $(this).find('[name=after_tax_total]').val();
    		if(total)
    			total_amount += parseFloat(total);
    	})
    	$('#goods_value').val(decimal(total_amount));
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