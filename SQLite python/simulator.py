# file simulator.py

import sqlite3 as lite

conn = lite.connect('world.db')
cur = conn.cursor()
tasksCur = conn.cursor()


def _isBusy(num):
    return cur.execute('SELECT status FROM workers WHERE id=?', [num]).fetchone()[0] == 'busy'


def _turnBusy(num):
    cur.execute('UPDATE workers SET status=? WHERE id=?', ("busy", num))
    name = cur.execute('SELECT name FROM workers WHERE id=?', [num]).fetchone()[0]
    print(name + " says: work work")


def _turnIdle(num):
    cur.execute('UPDATE workers SET status=? WHERE id=?', ("idle", num))
    name = cur.execute('SELECT name FROM workers WHERE id=?', [num]).fetchone()[0]
    print(name + " says: All Done!")


def _fixResources(name, cost):
    cur.execute('UPDATE resources SET amount=amount-? WHERE name=?', (cost, name))


def _printStatus(worker_id, task_name):
    name = cur.execute('SELECT name FROM workers WHERE id=?', [worker_id]).fetchone()[0]
    print(name + " is busy " + task_name + "...")


list = [] # indicates which tasks are undergoing work at the moment
while len(tasksCur.execute('SELECT * FROM tasks').fetchall()) > 0:
    tasksCur.execute('SELECT * FROM tasks')
    for row in tasksCur:
        if _isBusy(row[2]):  # worker is assigned
            if list.count(row[0]) > 0 and row[5] > 0:  # run a timecycle
                _printStatus(row[2], row[1])
                cur.execute('UPDATE tasks SET time_to_make=time_to_make-1 WHERE id=?', [row[0]])          
        else:  # assign a worker to a task
            _turnBusy(row[2])
            _fixResources(row[3], row[4])
            list.append(row[0])
    tasksCur.execute('SELECT * FROM tasks')
    for row in tasksCur:
        if row[5] == 0:
            _turnIdle(row[2])
            list.remove(row[0])
            cur.execute('DELETE FROM tasks WHERE id=?', [row[0]])  # remove task        	
conn.commit()
conn.close()
