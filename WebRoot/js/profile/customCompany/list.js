
$(document).ready(function() {
	document.title = '报关企业查询 | '+document.title;
	
	

    $('#menu_profile').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/customCompany/list",
        "columns": [
            { "data": "SHOP_NO"},
            { "data": "SHOP_NAME", 
              "render": function ( data, type, full, meta ) {
                    return "<a href='/customCompany/edit?id="+full.ID+"' >"+data+"</a>";
                }
            },
            { "data": "LEGAL_PERSON"},
            { "data": "CONTACT_PERSON"}, 
            { "data": "CONTACT_PHONE"}, 
            { "data": "COMPANY_PHONE"},
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"},
            { "data": null,
                "width": "10%",
                "render": function ( data, type, full, meta ) {
                        var str="<nobr>";
             
                    
                         if(full.IS_STOP == 'N'){
                                 str += "<a class='btn btn-danger  btn-sm' href='/customCompany/delete/"+full.ID+"'>"+
                                         "<i class='fa fa-trash-o fa-fw'></i>"+ 
                                         "停用"+
                                         "</a>";
                         }else{
                             str +="<a class='btn btn-success btn-sm' href='/customCompany/delete/"+full.ID+"'>"+
                                     "<i class='fa fa-trash-o fa-fw'></i>启用</a>";
                         }
                    
                    str +="</nobr>";
                   return str;
                }
            }
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#orderForm")[0].reset();
    });
    
     
    
    $('#searchBtn').click(function(){
        searchData(); 
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

    var searchData=function(){
    	var itemJson = buildCondition();
    	var url = "/customCompanyOrder/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
   
    

} );