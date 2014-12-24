# Profile Hidden Markov Models

Hidden Markov models are probabilistic finite state machines.Models like this.
![alltag](http://cse-wiki.unl.edu/wiki/images/e/ef/ProfileHMM.gif)
  - 'M' is a match state
  - 'D' is a delete state
  - 'I' is a insert state

Firstly , what is hidden markov models ? [Go Wikipedia HMM]  and what is profile hidden markov models? [Go Wikipedia pHMM] 

>What is match state ? In my code , match state is a count(char) bigger than count('.').

>What is insert state ? If we are not in match state and input string not equal '.' in this position.

>What is delete state ? If we are in match state and input string have '.' in this position.

#####In match state way:
Match(i) --> Match(i+1) and Match(i) -- > Delete(i+1) 

Insert(i) --> Match(i+1) and Insert(i) --> Delete(i+1)

Delete(i) --> Match(i+1) and Delete(i) --> Delete(i+1)
#####Not in match state way:
Match(i) --> Insert(i)

Insert(i) --> Insert(i)

Delete(i) --> Insert(i)
####What this will do ?

For example we have this inputs.
>S1 : EC....

>S2 : EC.E.G

>S3 : .CGEJG

>S4 : EG..JG

>S5 : EG...G

S1,S2,S3,S4 and S5 , we have five input and what is output ?

####t.out
Transition probability.M(0) is a start point and M(len(string)) is a end point

>1:('M0', {'strings': [0, 1, 2, 3, 4], 'D1': {'strings': [2], 'prob': 0.2}, 'M1': {'strings': [0, 1, 3, 4], 'prob': 0.8}}) 

>2:('D1', {'strings': [2], 'M2': {'strings': [2], 'prob': 1.0}})

>3:('M1', {'strings': [0, 1, 3, 4], 'M2': {'strings': [0, 1, 3, 4], 'prob': 1.0}})

>4:('M2', {'strings': [2, 0, 1, 3, 4], 'M3': {'strings': [4], 'prob': 0.2}, 'I2': {'strings': [2, 1, 3], 'prob': 0.6}, 'D3': {'strings': [0], 'prob': 0.2}})

>5:('I2', {'strings': [2, 1, 2, 2, 3], 'I2': {'strings': [2], 'prob': 0.4}, 'M3': {'strings': [1, 2, 3], 'prob': 0.6}})

>6:('M3', {'strings': [1, 2, 3, 4], 'M4': {'strings': [1, 2, 3, 4], 'prob': 1.0}})

>7:('D3', {'strings': [0], 'M4': {'strings': [0], 'prob': 1.0}})

>8:('M4', {'strings': [0, 1, 2, 3, 4]})

#####Transition path :

M0 --> D1 and Mo --> M1 

D1 --> M2 and M1 --> M2

M2 --> M3 and M2 --> I2 and M2 --> D3

I2 --> I2 and I2 --> M3 

M3 --> M4 and D3 --> M4 

M4 is a end point
####e.out
Emission probability.
>1 : ('M1', {'E': 0.8})

>2 : ('M2', {'G': 0.4, 'C': 0.6})

>3 : ('M3', {'G': 0.8})

>4 : ('M4', {}) //end point

**Free Software, Hell Yeah!**

[Go Wikipedia HMM]:https://en.wikipedia.org/wiki/Hidden_Markov_model
[Go Wikipedia pHMM]:https://en.wikipedia.org/wiki/Hidden_Markov_model
