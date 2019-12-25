(ns counter.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [counter.db-test]
            [counter.events-test]
            [counter.subs-test]))

(doo-tests '[counter.db-test
             counter.events-test
             counter.subs-test])
