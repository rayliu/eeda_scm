
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
        var cargo_table_rows = $("#cargo_table tr");
        var cargo_items_array=[];
        for(var index=0; index<cargo_table_rows.length; index++){
            if(index==0)
                continue;

            var row = cargo_table_rows[index];
            var id = $(row).attr('id');
            if(!id){
                id='';
            }

            var item={
                id: id,
                item_no: $(row.children[1]).find('input').val(), 
                qty: $(row.children[2]).find('input').val(),
                unit: $(row.children[3]).find('input').val(),
                price: $(row.children[4]).find('input').val(),
                total: $(row.children[5]).find('input').val(),
                action: $('#order_id').val().length>0?'UPDATE':'CREATE'
            };
            cargo_items_array.push(item);
        }

        //add deleted items
        for(var index=0; index<deletedTableIds.length; index++){
            var id = deletedTableIds[index];
            var item={
                id: id,
                action: 'DELETE'
            };
            cargo_items_array.push(item);
        }
        return cargo_items_array;
    };

    salesOrder.reDrawCargoTable=function(order){
        deletedTableIds=[];
        cargoTable.clear();
        for (var i = 0; i < order.ITEM_LIST.length; i++) {
            var item = order.ITEM_LIST[i];
            var item={
                "ID": item.ID,
                "ITEM_NO": item.ITEM_NO,
                "STY": item.STY,
                "UNIT": item.UNIT,
                "PRICE": item.PRICE,
                "TOTAL": item.TOTAL
            };
    
            cargoTable.row.add(item).draw(false);
        }       
    };

    //------------事件处理
    var cargoTable = $('#cargo_table').DataTable({
        "processing": true,
        "searching": false,
        "paging": false,
        "info": false,
        "autoWidth": true,
        "language": {
            "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
        },
        "createdRow": function ( row, data, index ) {
            $(row).attr('id', data.ID);
        },
        "columns": [
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "STY" ,
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "UNIT",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                    var str= '<select class="form-control search-control" disabled name="unit">'
              	   	   +'<option></option>'
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
                   return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            },
            { "data": "TOTAL",
                "render": function ( data, type, full, meta ) {
                    if(!data)
                        data='';
                   return '<input type="text" disabled value="'+data+'" class="form-control"/>';
                }
            }
        ]
    });

    $('#add_cargo').on('click', function(){
        var item={};
        cargoTable.row.add(item).draw(true);
    });

    
} );