method inputr(inp) vars rs
begin
rs := times(inp, 10);
return rs;
endmethod;

method main() vars x, res
begin
read x;
res := sum(x);
res := plus(res, 10);
write res;
endmethod;

method sum(inp) vars res, res2
begin
res:=0;
while less(0, inp)
begin
res := plus(res,inp);
inp := minus(inp,1);
endwhile;
res2 := inputr(x);
res := plus(res, res2);
return res;
endmethod;
