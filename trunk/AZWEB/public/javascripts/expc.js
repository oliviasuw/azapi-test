function toXML(d){
    var ret = "<" + d.name + " ";
    for (var v in d.vars){
        ret = ret + d.vars[v].display + "='" + d.vars[v].value + "' "; 
    }
    ret = ret + ">";
    for (var field in d){
        log("checking field " + field);
        if (d[field] instanceof Array && field != "vars"){
            log("good field!");
            for (var v in d[field]){
                ret = ret + toXML(d[field][v]);
            }
        }
    }
                
    ret = ret + "</" + d.name + ">";
    return ret;
}
            
// STARTING OF SCREEN CODE
function doLayout(){
    //MAIN LAYOUT
    $('body').layout({
        defaults: {
            fxName:               "slide"
            ,  
            fxSpeed:            "slow"
            ,  
            spacing_close:        14
            ,  
            spacing_open:	 0
        }
        ,  
        north: {
            fxName:                "none"
            , 
            resizable: false
            , 
            closable: false
            , 
            size: 50
                        
        }
    });
               
    //SCREEN LAYOUT                
    $('#screen-layout').layout({
        defaults: {
            fxName:               "slide"
            ,  
            fxSpeed:               "slow"
        }
        ,  
        north: {
            fxName:                "none"
            ,  
            spacing_closed:        8
            , 
            pane_spacing: 0
            , 
            resizable: false
            , 
            closable: false
            ,  
            spacing_open:	 0
        }
    });
}
            
function Leaf(data){
                
    this.init = function(data){
        this.display = data.display;
        this.name = data.name;
        this.description = data.description;
        this.vars = data.vars;
        this.caption = "unknown";
    }
                
    this.createNewItemDialog = function(title, icon , type, jsType){
        var welcomeHtml = $("<table cellspacing='0'> <tr> <td> <img src='" + icon + "'> </td> <td> <span class='welcome-text'> " + title + " </span> </td> </table>");
        new_item_dialog.setTitle("Add Items");
        var div = grid.getLengthDiv().html("");
        div.append(welcomeHtml);
                        
        grid.clear();
        server.getItems({
            of: type
        }, function(data){
            window.data = data;
            grid.load(map(data, function(d){
                return new jsType(d);
            }));
            log("setting multiple selection");
            grid.setMultipleSelection();
            new_item_dialog.showFullMaximumHeight(85);
        });

    }
                
    if (data != null){
        this.init(data);
    }
                
    this.loadScreen = function(){
        $("#node-control-caption").text(this.caption);
        add_links.clear();
        this._loadLinks();
        propGrid.clear();
        propGrid.load(this.vars);
    };
                
    this._loadLinks = function(){
    //empty
    }
}

Test.prototype = new Leaf();
function Test(data){
    this.init(data);
    this.pgens = [];
    this.scols = [];
    this.algs = [];
                
    this.icon = "/public/images/expc/test16.png";
    this.caption = "Test Properties";
    var athis = this;
    this._loadLinks = function(){
                    
        add_links.add({
            display: "Add Statistic Collector", 
            imgClass:"scol"
        }, function(){
            ok_link.selectionDone = function(selection){
                athis.scols = athis.scols.concat(selection);
            }
            athis.createNewItemDialog("Select Statistic Collector To Add", "/public/images/expc/statistic-col16.png", "StatisticCollector", Leaf);                        
        });
                    
        add_links.add({
            display:"Add Problem Generator", 
            imgClass:"pgen"
        }, function(){
            ok_link.selectionDone = function(selection){
                athis.pgens = athis.pgens.concat(selection);
            }

            athis.createNewItemDialog("Select Problem Generator To Add", "/public/images/expc/problem-gen16.png", "ProblemGenerator", Leaf);
        });
                    
        add_links.add({
            display:"Add Algorithm", 
            imgClass:"alg"
        }, function(){
            ok_link.selectionDone = function(selection){
                athis.algs = athis.algs.concat(selection);
            }
            
            athis.createNewItemDialog("Select Algorithm To Add", "/public/images/expc/algorithm16.png", "AlgorithmMetadata", Leaf);
        });
    };
}

Algorithm.prototype = new Leaf();
function Algorithm(data){
    this.init(data);
    this.icon = "/public/images/expc/algorithm16.png";
    this.caption = "Algorithm Properties";    
}

//CLASS: EXPERIMENT
Experiment.prototype = new Leaf();
function Experiment(){
    this.init({
        display: "Experiment", 
        name: "experiment", 
        description: "new Experiment", 
        vars: []
    });
    this.caption = "Experiment Properties";
    this.tests = [];
    this.icon = "/public/images/expc/experiment16.png";                
    var athis = this;
    this._loadLinks = function(){
        add_links.add({
            display: "Add Test", 
            imgClass: "test"
        },
        function(){
            ok_link.selectionDone = function(selection){
                window.selection = selection;
                athis.tests = athis.tests.concat(selection);
            }

            athis.createNewItemDialog("Select Tests To Add", "/public/images/expc/test16.png", "Test", Test);
        });
    };
}
            
//FUNCTION: MAIN
function main () {

    doLayout();
                
    //INITIALIZE COMPONENTS
    grid.initWithColumns(["display", "description"]);
    propGrid.initCleanWithColumns(["display", "description","value"], [2]);
                
                
    //DIALOG
    ok_link.selectionDone = function(){};
    ok_link.click(function(){
        var selected = grid.getSelected();
        each(selected, tree.appendToSelected);
        new_item_dialog.close();
        ok_link.selectionDone(selected);
    })
                
    //CREATE INITIAL EXPERIMENT SCREEN
    window.exp = new Experiment();
    exp.loadScreen();
                
    tree.addChild(exp);
    tree.onSelected(function(node){
        if (node != null){
            node.loadScreen();
        }
    });
                
//TESTING

};