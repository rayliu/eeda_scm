
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });

    //------------save
    $('#saveBtn').click(function(e){
        $(this).attr('disabled', true);

        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }

        var order = {
            id: $('#order_id').val(),
            shop_no: $('#shop_no').val(),  
            shop_name: $('#shop_name').val(),  
            legal_person: $('#legal_person').val(),
            contact_person: $('#contact_person').val(),
            contact_phone: $('#contact_phone').val(),  
            company_phone: $('#company_phone').val(),  
            website: $('#website').val(),
            address: $('#address').val(), 
            cus_code: $('#cus_code').val(),  
            ciq_code: $('#ciq_code').val(),  
            org_code: $('#org_code').val(),  
            warehouse_no: $('#warehouse_no').val(),
            ebp_code_cus: $('#ebp_code_cus').val(),
            ebp_code_ciq: $('#ebp_code_ciq').val(),
            ebp_name: $('#ebp_name').val(),
            ebc_code_cus: $('#ebc_code_cus').val(),
            ebc_code_ciq: $('#ebc_code_ciq').val(),
            ebc_name: $('#ebc_name').val(),
            agent_code_cus: $('#agent_code_cus').val(),
            agent_code_ciq: $('#agent_code_ciq').val(),
            agent_name: $('#agent_name').val()
        };

        console.log(order);

        //异步向后台提交数据
        $.post('/customCompany/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){	
            	$("#creator_name").val(data.CREATE_BY_NAME);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);

                $('#saveBtn').attr('disabled', false);       
            }else{
                $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
                $('#saveBtn').attr('disabled', false);
            }
        },'json').fail(function() {
            $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
            $('#saveBtn').attr('disabled', false);
          });
    });
    
} );