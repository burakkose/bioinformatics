from math import ceil

def _getMID(i): # get state from parameter
    return 'M{}'.format(i), 'I{}'.format(i), 'D{}'.format(i)

def _keyify(x): # for sort
    return int("{}".format(x[0][1:]))

# variables
states = ('M', 'I', 'D')  # HMM models states
observations = tuple('ACDEFGHIKLMNPQRSTVWY.')  # protein
transition_probability = {} # transition probability
emission_probability = {}   # emission probability

# Open file and read input String
with open("in.inp", 'r') as f:
    inputString = tuple(f.read().split('\n'))

num_of_strings = len(inputString)  # number of input strings
num_of_chars = len(inputString[0]) # number of char in one string

frequencyList = [{} for i in range(num_of_chars + 1)]

for i in inputString:
    j = 0
    for k in i:
        if k in frequencyList[j].keys():
            frequencyList[j][k] += 1
        else: 
            frequencyList[j][k] = 1
        j += 1

matchStatePossition = [k for n, k in zip(frequencyList, range(num_of_chars + 1)) 
               if int(n.get('.',0)) < ceil(num_of_strings/2)]
#State Lists
matchState  = ['M{}'.format(k) for k in range(0, len(matchStatePossition)+1)]
insertState = ['I{}'.format(k) for k in range(0, len(matchStatePossition))]
deleteState = ['D{}'.format(k) for k in range(1, len(matchStatePossition))]
#Transition Probability formatting
transition_probability.update({key : {'strings' : []} for key in matchState})
transition_probability.update({key : {'strings' : []} for key in insertState})
transition_probability.update({key : {'strings' : []} for key in deleteState})
# all input string put in begin state
transition_probability['M0']['strings'] = [n for n in range(num_of_strings)] 

i = 0 # counter
j = 0 # current state no

while i < num_of_chars + 1:
    M, I, D, = _getMID(j)
    nextM , nextI , nextD = _getMID(j+1)
    if i in matchStatePossition:  # if match state
        d_deleteList , d_matchList  = [],[] # D --> D List and D --> M List
        m_deleteList , m_matchList  = [],[] # M --> D List and M --> M List
        i_matchList  , i_deleteList = [],[] # I --> M List and I --> D List
        if i != 0:
            if transition_probability[D]['strings']:  # D --> D and D --> M
                # D(j) --> D(j+1) or D(j) --> M(j+1)
                try:
                    d_deleteList = [n for n in transition_probability[D]['strings'] if inputString[n][i] == '.']
                except Exception: 
                    pass
                d_matchList = [n for n in transition_probability[D]['strings'] if n not in d_deleteList]
                if d_deleteList: # if d_deleteLİst not empty
                    transition_probability[D][nextD] = {'prob' : float(len(d_deleteList) / 
                                                                       len(transition_probability[D]['strings'])),
                                                        'strings' : d_deleteList}         # D --> D
                    transition_probability[nextD]['strings'].extend(d_deleteList)
                if d_matchList: # if d_matchList not empty
                    transition_probability[D][nextM] = {'prob' : float(len(d_matchList) / # D --> M
                                                                       len(transition_probability[D]['strings'])),
                                                        'strings' : transition_probability[D]['strings']} 
                    transition_probability[nextM]['strings'].extend(d_matchList)
            if transition_probability[I]['strings']:  # I --> M and I --> D
                # I(j) --> D(j+1) or I(j) --> M(j+1)
                try:
                    i_deleteList = list(set([n for n in transition_probability[I]['strings'] 
                                             if inputString[n][i] == '.']))
                except Exception: 
                    pass
                i_matchList = list(set([n for n in transition_probability[I]['strings'] 
                                        if n not in i_deleteList]))
                if i_deleteList: # if i_deleteList not empty
                    transition_probability[I][nextD] = {'prob' : float(len(i_deleteList) / 
                                                                       len(transition_probability[I]['strings'])),
                                                        'strings' : i_deleteList} # I --> D
                    transition_probability[nextD]['strings'].extend(set(i_deleteList))    
                if i_matchList:  # if i_matchList not empty
                    transition_probability[I][nextM] = {'prob' : float(len(i_matchList) / 
                                                                       len(transition_probability[I]['strings'])),
                                                        'strings' : i_matchList}  # I --> M
                    transition_probability[nextM]['strings'].extend(set(i_matchList))
        if transition_probability[M]['strings']:  # M --> D and M --> M
            # M(j) --> D(j+1) or M(j) --> M(j+1)
            try:
                m_deleteList = [n for n in transition_probability[M]['strings'] 
                                if inputString[n][i] == '.']
            except Exception:
                pass
            m_matchList  = [n for n in transition_probability[M]['strings'] 
                            if n not in m_deleteList + transition_probability[I]['strings']]
            if m_deleteList: # if m_deleteList not empty
                transition_probability[M][nextD] = {'prob' : float(len(m_deleteList) / 
                                                                   len(transition_probability[M]['strings'])),
                                                    'strings' : m_deleteList} # M --> D
                transition_probability[nextD]['strings'].extend(m_deleteList)
            if m_matchList: # if m_matchList not empty
                transition_probability[M][nextM] = {'prob' :  float(len(m_matchList) / 
                                                                    len(transition_probability[M]['strings'])),
                                                    'strings' : m_matchList}  # M --> M
                transition_probability[nextM]['strings'].extend(m_matchList)
        j += 1
    else:
        insert_state_list = []
        while True: # loop for next insert state
            insert_state_list.extend([n for n in range(num_of_strings) 
                                      if inputString[n][i] != '.'])
            if i+1 in matchStatePossition or i+1 == num_of_chars:
                break # if i+1 no match state or i+1 last char in strings
            i += 1  # next insert state
        if insert_state_list: # if insert_state not empty
            come_from_match  = [n for n in transition_probability[M]['strings'] 
                                if n in insert_state_list]                      # M --> I
            come_from_del    = [n for n in transition_probability[D]['strings'] 
                                if n in insert_state_list]                      # D --> I
            come_from_insert = [n for n in set(insert_state_list) 
                                for k in range(insert_state_list.count(n)-1)]   # I --> I
            if come_from_match:  # if string come from match state
                transition_probability[M][I] = {'prob' :  float(len(come_from_match) / 
                                                                len(transition_probability[M]['strings'])),
                                                        'strings' : come_from_match} # M(j) --> I(j)
            if come_from_del:    # if string come from delete state
                transition_probability[D][I] = {'prob' :  float(len(come_from_del) / 
                                                                len(transition_probability[D]['strings'])),
                                                        'strings' : come_from_del}   # D(j) --> I(j)
            if come_from_insert: # if string come from insert state
                transition_probability[I][I] = {'prob' :  float(len(come_from_insert) / 
                                                                len(insert_state_list)),
                                                        'strings' : list(set(come_from_insert))}
            transition_probability[I]['strings'].extend(insert_state_list)
    # get emission probability without '.'
    emission_probability[nextM] = {n : round(frequencyList[i][n] / num_of_strings,3) 
                                   for n in frequencyList[i] if n != '.'} 
    i += 1
    
# write emission and transition probability
t = {n : transition_probability[n] for n in transition_probability 
     if transition_probability[n]['strings']}
t = sorted(t.items(),key = _keyify)
e = sorted(emission_probability.items(),key = _keyify)
print(*e, sep="\n", file=open("e.out", "w"))
print(*t, sep="\n", file=open("t.out", "w")) 
            
            
