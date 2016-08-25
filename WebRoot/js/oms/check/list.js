
$(document).ready(function() {
	document.title = '波次复核查询   | '+document.title;

    $('#menu_incoming').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/check/waveCheckList",
        "columns": [
			{ "width": "30px",
			    "render": function ( data, type, full, meta ) {
			    	return '<button type="button" class="comfirm btn btn-success btn-xs">确认复核</button> ';
			    }
			},
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/moveOrder/edit?id="+full.ID+"' >"+data+"</a>";
                }
            },
            { "data": "WAREHOUSE_NAME"},
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}, 
            { "data": "STATUS"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#waveCheckForm")[0].reset();
    });

    $('#waveCheckForm').on('keydown','input',function(e){
    	var key = e.which;
    	if(key == 13){
    		var id = $(this).attr('id');
    		if(id == 'wave_order'){
    			$('#cargo_barcode').focus();
    		}else{
    			if($('#wave_order').val()!='' && $('#cargo_barcode').val()!='')
    				searchWaveData();
    		}
    	}
    })

    buildCondition=function(){
    	var item = {};
    	var orderForm = $('#orderForm input,select');
    	for(var i = 0; i < orderForm.length; i++){
    		var name = orderForm[i].id;
        	var value =orderForm[i].value;
        	if(name){
        		item[name] = value;
        	}
    	}
        return item;
    };

    var searchWaveData=function(){
    	var itemJson = buildCondition();
    	var url = "/moveOrder/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
    
});