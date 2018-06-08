//
// Created by yonatan on 11/15/17.
//

#include "../include/Commands.h"
#include "../include/GlobalVariables.h"

BaseCommand::BaseCommand(string args):args(args) {};//Constructor
string BaseCommand::getArgs() {
    return args;
}

bool isPath(string path){
    if (path.find("/")==path.npos){
        return false;
    }
    return true;
} //checks a string for '/' appearances
Directory* findChildrenByName(string name, const vector<BaseFile*> &v){
    if (v.size() == 0){
        return nullptr;
    }else{
        for (size_t i = 0; i < v.size(); i++) {
            if(v[i]->getName()==name) {
                if (v[i]->typeCheck()) { return dynamic_cast<Directory *>(v[i]); }
                else { return nullptr; }
            }
        }
    }
    return nullptr;
}

PwdCommand::PwdCommand(string args):BaseCommand(args){}; //Constructor
void PwdCommand::execute(FileSystem &fs) {
    cout<< fs.getWorkingDirectory().getAbsolutePath()<<endl;
}
string PwdCommand::toString() {return "pwd";}
BaseCommand* PwdCommand::clone() {
    return new PwdCommand(getArgs());
}

CdCommand::CdCommand(string args):BaseCommand(args) {}; //Constructor
void CdCommand::execute(FileSystem &fs) {
    string path = getArgs();
    Directory* pwd = &fs.getWorkingDirectory();
    if(path[0]=='/'){
        pwd=&fs.getRootDirectory();
        path=path.substr(1);
    }
    if(path.length()==0){
        fs.setWorkingDirectory(&fs.getRootDirectory());
        return; }
    while(isPath(path)){
        int split = path.find('/');
        string next=("");
        for (int i = 0; i < split; ++i) {
            next+=path[i];
        }
        path=path.substr(split+1);
        if(next == ".."){
            if(pwd==&fs.getRootDirectory()){
                cout<<"The system cannot find the path specified"<<endl;
                return;
            }
            pwd=pwd->getParent();
        }else{
            Directory *f = findChildrenByName(next, pwd->getChildren());
            if (f==nullptr){
                cout<<"The system cannot find the path specified"<<endl;
                return;
            }else{
                pwd=f;
            }
        }
    }//finished navigating path holds <name> of last dir
    if(path == ".."){
        if(pwd==&fs.getRootDirectory()){
            cout<<"The system cannot find the path specified"<<endl;
            return;
        }
        fs.setWorkingDirectory(pwd->getParent());
    }else {
        pwd = findChildrenByName(path, pwd->getChildren());
        if (pwd == nullptr) {
            cout << "The system cannot find the path specified" << endl;
            return;
        } else {
            fs.setWorkingDirectory(pwd);
        }
    }
}
string CdCommand::toString() { return "cd"; }
BaseCommand* CdCommand::clone() {
    return new CdCommand(getArgs());
}


void LsCommand::lsPrint(Directory &pwd){
    vector<BaseFile*> v = pwd.getChildren();
    for (size_t i = 0; i < v.size(); ++i) {
        if(v[i]->typeCheck()){
            cout<<"DIR\t"<< v[i]->getName() <<"\t"<< v[i]->getSize()<<endl;
        }else{
            cout<<"FILE\t"<< v[i]->getName() <<"\t"<< v[i]->getSize()<<endl;
        }
    }
}// Prints out the subdirectories
LsCommand::LsCommand(string args) :BaseCommand(args){}; //Constructor
void LsCommand::execute(FileSystem &fs) {
    string command = getArgs();
    Directory *pwd = &fs.getWorkingDirectory();
    if(command.length()==0 || command.compare("-s")==0){
        if(command.length()==0){//prints pwd contents by name
            fs.getWorkingDirectory().sortByName();
            lsPrint(fs.getWorkingDirectory());
        }else{//prints pwd contents by size
            fs.getWorkingDirectory().sortBySize();
            lsPrint(fs.getWorkingDirectory());
        }
    }else if (command.find("-s ")==0){ //prints sorted by size, handles [-s] modifier
        command=command.substr(3);
        CdCommand cd(command);
        cd.execute(fs);
        if(pwd == &fs.getWorkingDirectory()){return;}
        LsCommand ls("-s");
        ls.execute(fs);
    }else{ //prints sorted by name
        CdCommand cd(command);
        cd.execute(fs);
        if(pwd == &fs.getWorkingDirectory()){return;}
        LsCommand ls("");
        ls.execute(fs);
    }
    fs.setWorkingDirectory(pwd);
}
string LsCommand::toString() {return "ls";};
BaseCommand* LsCommand::clone() {
    return new LsCommand(getArgs());
}


bool nameCheck(string name){
    bool valid;
    if(name.length()==0){ return false;}
    string legal ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    for(size_t i=0; i<name.length(); i++){
        valid=false;
        for(size_t j=0; j<legal.length() && !valid; j++){
            if(name[i]==legal[j]){
                valid = true;
            }
        }
        if(!valid){return false;};
    }
    return true;
} //name is valid: a-z, A-Z, 0-9 chars only
bool nameExists(string name, const vector<BaseFile*> &v){
    for (size_t i = 0; i < v.size(); ++i) {
        if(v[i]->getName()==name){
            return true;
        }
    }
    return false;
} //given name exists @ target folder

MkdirCommand::MkdirCommand(string args):BaseCommand(args){};//Constructor
string MkdirCommand::toString() {return "mkdir";}
void MkdirCommand::execute(FileSystem &fs) {
    Directory *pwd = &fs.getWorkingDirectory();
    string path = getArgs();
    if(path[0] == '/'){
        pwd=&fs.getRootDirectory();
        path=path.substr(1);
    }
    while(isPath(path)) {
        int split = path.find("/");
        string next = ("");
        for (int i = 0; i < split; ++i) {
            next +=path[i];
        }
        path = path.substr(split + 1);
        if (next == "..") {
            pwd = pwd->getParent();
        } else {
            Directory *nextDir = findChildrenByName(next, pwd->getChildren());
            if (nextDir == nullptr) {//create subfolder to continue
                if (!nameCheck(next)) {
                    cout << "Illegal name input" << endl;
                    return;
                }
                if(nameExists(next, pwd->getChildren())){
                    cout<<"The directory already exists"<<endl;
                    return;
                }
                Directory *newDir = new Directory(next, pwd);
                pwd->addFile(newDir);
                pwd=newDir;
            } else {
                pwd = nextDir;
            }
        }
    }
    if(!nameCheck(path)){
        cout<<"Illegal name input"<<endl;
        return; }
    if(nameExists(path, pwd->getChildren())){
        cout<<"The directory already exists"<<endl;
        return; }
    Directory *dir=new Directory(path, pwd);
    pwd->addFile(dir);
}
BaseCommand* MkdirCommand::clone() {
    return new MkdirCommand(getArgs());
}

MkfileCommand::MkfileCommand(string args):BaseCommand(args) {};//Constructor
string MkfileCommand::toString() {return "mkfile";};
void MkfileCommand::execute(FileSystem &fs) {
    string path = getArgs();
    Directory *pwd = &fs.getWorkingDirectory();
    if(path[0]=='/'){
        pwd=&fs.getRootDirectory();
        path=path.substr(1);
    }
    while(isPath(path)) {
        int split = path.find("/");
        string next = ("");
        for (int i = 0; i < split; ++i) {
            next +=path[i];
        }
        path = path.substr(split + 1);
        if (next == "..") {
            pwd = pwd->getParent();
        } else {
            Directory *nextDir = findChildrenByName(next, pwd->getChildren());
            if (nextDir == nullptr) {//create subfolder to continue
                cout<<"The system cannot find the path specified"<<endl;
                return;
            } else {
                if (!nextDir->typeCheck()) {
                    cout << "The directory already exists" << endl;
                    return;
                }
                pwd = nextDir;
            }
        }
    }//finished navigating path holds <name> <size>
    int spaceLoc = path.find(" ");
    string fname=path.substr(0,spaceLoc);
    string fsize=path.substr(spaceLoc+1);
    int size=stoi(fsize);
    if(!nameCheck(fname)){
        cout<<"Illegal name input"<<endl;
        return;
    }
    if(nameExists(fname, pwd->getChildren())){
        cout<<"File already exists"<<endl;
        return; }

    File *f = new File(fname, size);
    pwd->addFile(f);
}
BaseCommand* MkfileCommand::clone() {
    return new MkfileCommand(getArgs());
}

BaseFile* findFileByName(string name, const vector<BaseFile*> &v){
    if(v.size() == 0) {
        return nullptr;
    }else {
        for (size_t i = 0; i < v.size(); ++i) {
            if(v[i]->getName() == name) {
                 return v[i];
            }
        }
        return nullptr;
    }
}


CpCommand::CpCommand(string args):BaseCommand(args){};//Constructor
string CpCommand::toString() {return "cp";}
void CpCommand::execute(FileSystem &fs) {
    string path = getArgs();
    Directory *src = &fs.getWorkingDirectory();
    Directory *dest = src;
    int spaceLoc=path.find(" ");
    string srcPath = path.substr(0, spaceLoc);
    string destPath = path.substr(spaceLoc+1);
    if(srcPath[0]=='/'){
        src=&fs.getRootDirectory();
        srcPath=srcPath.substr(1);
    }
    if(destPath[0]=='/'){
        dest=&fs.getRootDirectory();
        destPath=destPath.substr(1);
    }
    while(isPath(srcPath)){
        int split = srcPath.find("/");
        string next = "";
        for (int i = 0; i < split; ++i) {
            next+=srcPath[i];
        }
        srcPath=srcPath.substr(split+1);
        if(next == ".."){
            if(src==&fs.getRootDirectory()){
                cout<<"No such file or directory"<<endl;
                return;
            }
            src=src->getParent();
        }else{
            Directory *f = findChildrenByName(next, src->getChildren());
            if(f==nullptr){
                cout<<"No such file or directory"<<endl;
                return;
            }else{
                src=f;
            }
        }
    }//src = target file/directory's parent
    while(isPath(destPath)){
        int split = destPath.find("/");
        string next = "";
        for (int i = 0; i < split; ++i) {
            next+=destPath[i];
        }
        destPath=destPath.substr(split+1);
        if(next == ".."){
            if(dest==&fs.getRootDirectory()){
                cout<<"No such file or directory"<<endl;
                return;
            }
            dest=dest->getParent();
        }else{
            Directory *f = findChildrenByName(next, dest->getChildren());
            if(f==nullptr){
                cout<<"No such file or directory"<<endl;
                return;
            }else{
                dest=f;
            }
        }
    }//dest = target file/directory's parent
    if(destPath.length()>0) {
        if(destPath==".."){
            if(dest==&fs.getRootDirectory()){
                cout<<"No such file or directory"<<endl;
                return;
            }
            dest=dest->getParent();
            destPath="";
        }
        if(destPath!="") {
            Directory *f = findChildrenByName(destPath, dest->getChildren());
            if (f == nullptr) {
                cout << "No such file or directory" << endl;
                return;
            } else {
                dest = f;
            }
        }
    }//dest = target folder to copy in to
    BaseFile *toCopy = findFileByName(srcPath, src->getChildren());
    if(toCopy == nullptr){
        cout<<"No such file or directory"<<endl;
        return;
    }else{
        if(nameExists(toCopy->getName(), dest->getChildren())){
            //cout<<"File with given name already exists"<<endl;
            return; }
        if(toCopy->typeCheck()){//toCopy is a directory, deep copy needed
            Directory *dir = new Directory(dynamic_cast<Directory&>(*toCopy));
            dir->setParent(dest);
            dest->addFile(dir);
        } else{
            dest->addFile(new File(dynamic_cast<File&>(*toCopy)));
        }
    }
}
BaseCommand* CpCommand::clone() {
    return new CpCommand(getArgs());
}

MvCommand::MvCommand(string args):BaseCommand(args) {};//Constructor
string MvCommand::toString() {return "mv";}
void MvCommand::execute(FileSystem &fs) {
    string path = getArgs();
    Directory *src = &fs.getWorkingDirectory();
    Directory *dest = src;
    int spaceLoc = path.find(" ");
    string srcPath = path.substr(0, spaceLoc);
    string destPath = path.substr(spaceLoc + 1);
	if(srcPath == destPath){
		return;
	}
    if (srcPath[0] == '/') {
        src = &fs.getRootDirectory();
        srcPath = srcPath.substr(1);
    }
    if (destPath[0] == '/') {
        dest = &fs.getRootDirectory();
        destPath = destPath.substr(1);
    }
    while (isPath(srcPath)) {
        int split = srcPath.find("/");
        string next = "";
        for (int i = 0; i < split; ++i) {
            next += srcPath[i];
        }
        srcPath = srcPath.substr(split + 1);
        if (next == "..") {
            if(src==&fs.getRootDirectory()){
                cout<<"No such file or directory"<<endl;
                return;
            }
            src = src->getParent();
        } else {
            Directory *f = findChildrenByName(next, src->getChildren());
            if (f == nullptr) {
                cout << "No such file or directory" << endl;
                return;
            } else {
                src = f;
            }
        }
    }//src = target file/directory's parent
    while (isPath(destPath)) {
        int split = destPath.find("/");
        string next = "";
        for (int i = 0; i < split; ++i) {
            next += destPath[i];
        }
        destPath = destPath.substr(split + 1);
        if (next == "..") {
            if(dest==&fs.getRootDirectory()){
                cout<<"No such file or directory"<<endl;
                return;
            }
            dest = dest->getParent();
        } else {
            if(destPath==".."){
                if(dest==&fs.getRootDirectory()){
                    cout<<"No such file or directory"<<endl;
                    return;
                }
                dest = dest->getParent();
            }
            Directory *f = findChildrenByName(next, dest->getChildren());
            if (f == nullptr) {
                cout << "No such file or directory" << endl;
                return;
            } else {
                dest = f;
            }
        }
    }//dest = target file/directory's parent
    if (destPath.length() > 0) {
        if(destPath==".."){
            if(dest==&fs.getRootDirectory()){
                cout<<"Can't move directory"<<endl;
                return;
            }
            dest=dest->getParent();
            destPath="";
        }
        if(destPath!="") {
            Directory *f = findChildrenByName(destPath, dest->getChildren());
            if (f == nullptr) {
                cout << "No such file or directory" << endl;
                return;
            } else {
                dest = f;
            }
        }
    }//dest = target folder to be moved to
    if(srcPath==".."){
        cout<<"Can't move directory"<<endl;
        return;
    }
    BaseFile *toMove = findFileByName(srcPath, src->getChildren());
    if (toMove == nullptr) {
        cout << "No such file or directory" << endl;
        return;
    } else {
        if(toMove->typeCheck()){//toMove is a directory
            string toMoveAbs= dynamic_cast<Directory&>(*toMove).getAbsolutePath();
            string pwdAbs = fs.getWorkingDirectory().getAbsolutePath();
            if(pwdAbs.find(toMoveAbs)==0){
                cout<<"Can't move directory"<<endl;
                return;}
        }else{//toMove is a file
            string toMoveAbs= src->getAbsolutePath() + "/" + toMove->getName();
            string pwdAbs = fs.getWorkingDirectory().getAbsolutePath();
            if(pwdAbs.find(toMoveAbs)==0){
                cout<<"Can't move directory"<<endl;
                return;}
        }
        if (nameExists(toMove->getName(), dest->getChildren())) {
            //cout << "File with given name already exists" << endl;
            return;
        }
        if (toMove->typeCheck()) {//toMove is a directory, deep steal needed
            Directory *moved = new Directory(dynamic_cast<Directory&>(*toMove));
            moved->setParent(dest);
            dest->addFile(moved);
            src->removeFile(toMove);
        } else {
            File *file = new File(toMove->getName(), toMove->getSize());
            src->removeFile(toMove);
            dest->addFile(file);
        }
    }
}
BaseCommand* MvCommand::clone() {
    return new MvCommand(getArgs());
}

RenameCommand::RenameCommand(string args):BaseCommand(args){}; //Constructor
string RenameCommand::toString() {return "rename";}
void RenameCommand::execute(FileSystem &fs) {
    string path = getArgs();
    Directory* target = &fs.getWorkingDirectory();
    int spaceLoc = path.find(" ");
    string newName = path.substr(spaceLoc+1);
    path=path.substr(0,spaceLoc);
    if(path[0] == '/'){
        target=&fs.getRootDirectory();
        path=path.substr(1);
    }
    if(path.length()==0){
        cout<<"Can't rename root directory"<<endl;
        return;
    }
    if(path.length()>0) {
        while (isPath(path)) {
            int split = path.find("/");
            string next = path.substr(0, split);
            path = path.substr(split + 1);

            if (next == "..") {
                target = target->getParent();
            }else{
                Directory* nextDir = findChildrenByName(next, target->getChildren());
                if (nextDir == nullptr || !nextDir->typeCheck()){
                    cout<<"No such file or directory"<<endl;
                    return;}
                target=nextDir;
            }
        }
    }//target = the target file/folder parent
    BaseFile* file = findFileByName(path, target->getChildren());
    if (file == nullptr){
        cout<<"No such file or directory"<<endl;
        return;}
    if(!nameCheck(newName)){
        cout<<"Illegal name input"<<endl;
        return;}
    if(nameExists(newName, target->getChildren())){
        //cout<<"File with that name already exists"<<endl;
        return;}
    if(file==&fs.getWorkingDirectory()){
        cout<<"Can't rename the working directory"<<endl;
        return;}
    file->setName(newName);
}
BaseCommand* RenameCommand::clone() {
    return new RenameCommand(getArgs());
}

RmCommand::RmCommand(string args):BaseCommand(args){}; //Constructor
string RmCommand::toString() {return "rm";}
void RmCommand::execute(FileSystem &fs) {
    Directory* savedPwd = &fs.getWorkingDirectory();
    string command = getArgs();
    if(&fs.getRootDirectory()==savedPwd && command=="/"){
        cout<<"Can't remove directory"<<endl;
        return;
    }

    int split = command.find_last_of("/");

    string name = command.substr(split+1);
    command=command.substr(0, split);
    if(command!=name){
        CdCommand cd(command);
        cd.execute(fs);
        if(&fs.getWorkingDirectory() == savedPwd){
            return;
        }
    }
    BaseFile* target=findFileByName(name, fs.getWorkingDirectory().getChildren());
    if (target == nullptr){
        cout<<"No such file or directory"<<endl;
        fs.setWorkingDirectory(savedPwd);
        return;}
    if(target->typeCheck()){//target is a directory
        string targetAbs= dynamic_cast<Directory&>(*target).getAbsolutePath();
        string pwdAbs = savedPwd->getAbsolutePath();
        if(pwdAbs.find(targetAbs)==0){
            cout<<"Can't remove directory"<<endl;
            fs.setWorkingDirectory(savedPwd);
            return;}
    }else {//target is a file
        string targetAbs = fs.getWorkingDirectory().getAbsolutePath() + "/" + target->getName();
        string pwdAbs = savedPwd->getAbsolutePath();
        if (pwdAbs.find(targetAbs) == 0) {
            cout << "Can't remove file" << endl;
            fs.setWorkingDirectory(savedPwd);
            return;
        }
    }
    fs.getWorkingDirectory().removeFile(target);
    fs.setWorkingDirectory(savedPwd);
}
BaseCommand* RmCommand::clone() {
    return new RmCommand(getArgs());
}

HistoryCommand::HistoryCommand(string args, const vector<BaseCommand *> &history):BaseCommand(args), history(history){}; //Constructor
string HistoryCommand::toString() {return "history";}
void HistoryCommand::execute(FileSystem &fs) {
    for (size_t i = 0; i <history.size() ; ++i) {
        if(dynamic_cast<ErrorCommand*>(history[i])){
            cout << i << "\t" << history[i]->toString()<< endl;
        }else {
            cout << i << "\t" << history[i]->toString() << " " << history[i]->getArgs() << endl;
        }
    }
}
BaseCommand* HistoryCommand::clone() {
    return new HistoryCommand(getArgs(), vector<BaseCommand*>());
}

VerboseCommand::VerboseCommand(string args):BaseCommand(args){}; //Constructor
string VerboseCommand::toString() {return "verbose";}
void VerboseCommand::execute(FileSystem &fs) {
    string argument = getArgs();
    int input = stoi(argument);
    if(input > 3 || input < 0){
        cout<<"Wrong verbose input"<<endl;
        return;}
    if (input == 0){ verbose=0;}
    else if (input == 1) { verbose=1;}
    else if (input == 2) { verbose=2;}
    else { verbose=3;}
}//Implement this!
BaseCommand* VerboseCommand::clone() {
    return new VerboseCommand(getArgs());
}

ExecCommand::ExecCommand(string args, const vector<BaseCommand *> &history):BaseCommand(args), history(history){}; //Constructor
string ExecCommand::toString() {return "exec";}
void ExecCommand::execute(FileSystem &fs) {
    string argument = getArgs();
    if(!execCmdCheck(argument)){
        cout<<"Illegal input"<<endl;
        return;}
    int cmdNum = stoi(argument);
    if(cmdNum<0 || (size_t)cmdNum>history.size()){
        cout<<"Command not found"<<endl;
        return;}
    BaseCommand* cmd = history[cmdNum];
    cmd->execute(fs);
}
bool ExecCommand::execCmdCheck(string arg){
    bool valid;
    if(arg.length()==0){ return false;}
    string legal ="0123456789";
    for(size_t i=0; i<arg.length(); i++){
        valid=false;
        for(size_t j=0; j<legal.length() && !valid; j++){
            if(arg[i]==legal[j]){
                valid = true;
            }
        }
        if(!valid){return false;};
    }
    return true;
}
BaseCommand* ExecCommand::clone() {
    return new ExecCommand(getArgs(), vector<BaseCommand*>());
}



ErrorCommand::ErrorCommand(string args):BaseCommand(args){};
string ErrorCommand::toString() {return getArgs();}
void ErrorCommand::execute(FileSystem &fs) {
    string argument = getArgs();
    int spaceLoc = argument.find(" ");
    string cmd = argument.substr(0, spaceLoc);
    cout <<cmd<<": Unknown command"<<endl;
}
BaseCommand* ErrorCommand::clone() {
    return new ErrorCommand(getArgs());
}


