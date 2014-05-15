BaseObject={super=nil;name='Object';new=function(class)
 local OTOSOTE1={class=class;};
 local OTOSOTE2={__index=function(self,key)
return class.methods[key];
end;};
setmetatable(OTOSOTE1,OTOSOTE2);
return OTOSOTE1;
end;methods={classname=function(self)
return (self.class.name);
end;};data={};};
function OTOSOTE3(name,super)
if (super == nil) then 
super=BaseObject;
end;
 local OTOSOTE5={super=super;name=name;new=function(self,...)
 local OTOSOTE1=super.new(self,'___CREATE_ONLY___');
if (super.methods.init) then 
OTOSOTE1.init_super=super.methods.init;
end;
if (self.methods.init) then 
if (tostring(arg[1]) ~= '___CREATE_ONLY___') then 
OTOSOTE1.init=self.methods.init;
if OTOSOTE1.init then 
OTOSOTE1:init(unpack(arg));
end;
end;
end;
return OTOSOTE1;
end;methods={};};
setmetatable(OTOSOTE5,{__index=function(self,key)
return self.super[key];
end;__call=function(self,...)
return self.new(self,unpack(arg));
end;});
setmetatable(OTOSOTE5.methods,{__index=function(self,key)
return OTOSOTE5.super.methods[key];
end;});
return OTOSOTE5;
end;
cAnimal=OTOSOTE3('Animal');
function cAnimal.methods:init(action,cutename)
self.superaction=action;
self.supercutename=cutename;
end;
cTiger=OTOSOTE3('Tiger',cAnimal);
function cTiger.methods:init(cutename)
self:init_super('HUNT (Tiger)','Zoo Animal (Tiger)');
self.action='ROAR FOR ME!!';
self.cutename=cutename;
end;
Tiger1=cAnimal:new('HUNT','Zoo Animal');
Tiger2=cTiger:new('Mr Grumpy');
Tiger3=cTiger:new('Mr Hungry');
a=Tiger1:classname();
b=Tiger2:classname();
c=Tiger3:classname();
print('CLASSNAME FOR TIGER1 = ',a);
print('CLASSNAME FOR TIGER2 = ',b);
print('CLASSNAME FOR TIGER3 = ',c);
a=Tiger1.superaction;
b=Tiger1.supercutename;
c=Tiger1.action;
d=Tiger1.cutename;
print('===============');
print('SUPER ACTION',a);
print('SUPER CUTENAME',b);
print('ACTION        ',c);
print('CUTENAME',d);
print('===============');
a=Tiger2.superaction;
b=Tiger2.supercutename;
c=Tiger2.action;
d=Tiger2.cutename;
print('SUPER ACTION',a);
print('SUPER CUTENAME',b);
print('ACTION        ',c);
print('CUTENAME',d);
print('===============');
a=Tiger3.superaction;
b=Tiger3.supercutename;
c=Tiger3.action;
d=Tiger3.cutename;
print('SUPER ACTION',a);
print('SUPER CUTENAME',b);
print('ACTION        ',c);
print('CUTENAME',d);