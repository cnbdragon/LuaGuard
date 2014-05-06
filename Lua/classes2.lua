---------------------------------------------------
---- SETCLASS CLONES THE BASIC OBJECT CLASS TO CREATE NEW CLASSES
-----------------------------------------------------
-- Supports INHERITANCE 
--
-- Sam Lie, 17 May 2004 
-- Modified Code from Christian Lindig - lindig (at) cs.uni-sb.de
---------------------------------------------------------------

-- EVERYTHING INHERITS FROM THIS BASIC OBJECT CLASS
BaseObject = {
  super   = nil,
  name    = "Object",
  new     =
  function(class)
    local obj  = {class = class}
    local meta = {
      __index = function(self,key) return class.methods[key] end 
    }            
    setmetatable(obj,meta)
    return obj
  end,
  methods = {classname = function(self) return(self.class.name) end},
  data    = {}
}

function setclass(name, super)
  if (super == nil) then
    super = BaseObject
  end

  local class = {
    super = super; 
    name  = name; 
    new   =
    function(self, ...) 
      local obj = super.new(self, "___CREATE_ONLY___");
      -- check if calling function init
      -- pass arguments into init function
      if (super.methods.init) then
        obj.init_super = super.methods.init
      end

      if (self.methods.init) then
        if (tostring(arg[1]) ~= "___CREATE_ONLY___") then
          obj.init = self.methods.init
          if obj.init then
            obj:init(unpack(arg))
          end
        end
      end

      return obj
    end,  
    methods = {}
  }

  -- if class slot unavailable, check super class
  -- if applied to argument, pass it to the class method new        
  setmetatable(class, {
    __index = function(self,key) return self.super[key] end,
    __call  = function(self,...) return self.new(self,unpack(arg)) end 
  })

  -- if instance method unavailable, check method slot in super class    
  setmetatable(class.methods, {
    __index = function(self,key) return class.super.methods[key] end
  })
  return class
end    

cAnimal=setclass("Animal")

function cAnimal.methods:init(action, cutename) 
  self.superaction = action
  self.supercutename = cutename
end

--==========================

cTiger=setclass("Tiger", cAnimal)

function cTiger.methods:init(cutename) 
  self:init_super("HUNT (Tiger)", "Zoo Animal (Tiger)")
  self.action = "ROAR FOR ME!!"
  self.cutename = cutename
end

--==========================

Tiger1 = cAnimal:new("HUNT", "Zoo Animal")
Tiger2 = cTiger:new("Mr Grumpy")
Tiger3 = cTiger:new("Mr Hungry")

a=Tiger1:classname()
b=Tiger2:classname()
c=Tiger3:classname()
print("CLASSNAME FOR TIGER1 = ", a) 
print("CLASSNAME FOR TIGER2 = ", b) 
print("CLASSNAME FOR TIGER3 = ", c) 
a=Tiger1.superaction
b=Tiger1.supercutename
c=Tiger1.action
d=Tiger1.cutename
print("===============")
print("SUPER ACTION",a)
print("SUPER CUTENAME",b)
print("ACTION        ",c)
print("CUTENAME",d)
print("===============")
a=Tiger2.superaction
b=Tiger2.supercutename
c=Tiger2.action
d=Tiger2.cutename
print("SUPER ACTION",a)
print("SUPER CUTENAME",b)
print("ACTION        ",c)
print("CUTENAME",d)
print("===============")
a=Tiger3.superaction
b=Tiger3.supercutename
c=Tiger3.action
d=Tiger3.cutename
print("SUPER ACTION",a)
print("SUPER CUTENAME",b)
print("ACTION        ",c)
print("CUTENAME",d)
