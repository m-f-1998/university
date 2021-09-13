function load_directory_list(){
  var dirSel = document.getElementById("dir_select");
  var current_dir = document.getElementById("current_dir")

  dirSel.options[dirSel.options.length] = new Option(maindirectory, maindirectory);
  for (var dir in subdirectories) {
    console.log(subdirectories[dir]);
    if(!subdirectories[dir].startsWith(".")){
      dirSel.options[dirSel.options.length] = new Option(subdirectories[dir], subdirectories[dir]);
    }
  }

  current_dir.innerHTML = fulldirectory
}
