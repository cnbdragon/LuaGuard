Account = {}
Account.__index = Account

function Account.create(balance)
  local acnt = {}             -- our new object
  setmetatable(acnt,Account)  -- make Account handle lookup
  acnt.balance = balance      -- initialize our object
  return acnt
end

function Account:withdraw(amount)
  self.balance = self.balance - amount
end

-- create and use an Account
acc = Account.create(1000)
print(acc.balance)
acc:withdraw(100)
print(acc.balance)
