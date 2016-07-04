$(document).ready(function() {
	if(warehouse_name){
		document.title = warehouse_name+' | '+document.title;
	}
    $('#menu_profile').addClass('active').find('ul').addClass('in');
    
    $('#saveBtn').click(function(){
    	 var order = {
    	            id: $('#order_id').val(),
    	            warehouse_name: $('#warehouse_name').val(),  
    	            location: $('#location').val(),  
    	            warehouse_address: $('#warehouse_address').val(),  
    	            warehouse_area: $('#warehouse_area').val(),
    	            notify_mobile: $('#notify_mobile').val(),
    	            warehouse_area: $('#warehouse_area').val(),
    	            warehouse_desc: $('#warehouse_desc').val()
    	        };
    	$('#saveBtn').attr('disabled',true);
    	$.post('/warehouse/save',{params:JSON.stringify(order)},function(data){
    		if(data){
    			$.scojs_message('保存成功', $.scojs_message.TYPE_OK);
    			contactUrl("edit?id",data.ID);
    		}else{
    			$.scojs_message('保存失败', $.scojs_message.TYPE_FALSE);
    		}
    		$('#saveBtn').attr('disabled',false);
    	})
    })
    


} );