#ifndef _CPP_TEST_H
#define _CPP_TEST_H

#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <vector>
#include <exception>
#include <stdexcept>
#include <fstream>
#include <list>
#include <sstream>
#include <algorithm>
#include <utility>
#include "System.h"

struct __TEST__SUITE__STRUCT {
  int passed;
  int failed;
  int total;
} __test__suite__struct;

#define TEST_SUITE_PASSED	(__test__suite__struct.passed)
#define TEST_SUITE_FAILED	(__test__suite__struct.failed)
#define TEST_SUITE_TOTAL	(__test__suite__struct.total)

#define TEST_SUITE_PASSED_RATIO	(100.0 * TEST_SUITE_PASSED / TEST_SUITE_TOTAL)
#define TEST_SUITE_FAILED_RATIO	(100.0 * TEST_SUITE_FAILED / TEST_SUITE_TOTAL)

#define BEGIN_TEST_SUITE(name)	\
void name()\
{\
  std::cerr << ">>>\tTestSuite " #name "\n";\
  TEST_SUITE_PASSED = 0;\
  TEST_SUITE_FAILED = 0;\
  TEST_SUITE_TOTAL = 0;

#define END_TEST_SUITE	\
  std::cerr << "\n>>>\t";\
  std::cerr << "Passed: " << TEST_SUITE_PASSED << "(" << TEST_SUITE_PASSED_RATIO << "%)\t";\
  std::cerr << "Failed: " << TEST_SUITE_FAILED << "(" << TEST_SUITE_FAILED_RATIO << "%)\t";\
  std::cerr << "Total: " << TEST_SUITE_TOTAL << "\n";\
  std::cerr << ">>>\tEnd Of TestSuite.\n\n";\
}

#define BEGIN_TEST_CASE(name)	\
bool name()\
{\
	System::prepare();

#define END_TEST_CASE	\
  return true;\
}

#define RUN_TEST_CASE(name)	do {\
  bool pass = name(); ++TEST_SUITE_TOTAL;\
  if (pass) { std::cerr << '.'; ++TEST_SUITE_PASSED; }\
  /*else { std::cerr << ' ' << #name << ' '; ++TEST_SUITE_FAILED; }*/\
  else { std::cerr << 'X'; ++TEST_SUITE_FAILED; }\
} while (0)

#define assertNull(x)	do {\
  if ((x) != NULL) { return false; }\
} while (0)

#define assertNotNull(x)	do {\
  if ((x) == NULL) { return false; }\
} while (0)

#define assertTrue(x)   do {\
  if (!(x)) { return false; }\
} while (0)

#define assertFalse(x)  do {\
  if (x) { return false; }\
} while (0)

#define assertEmpty(v)	do {\
  if ((v).size()) { return false; }\
} while (0)

#define assertNotEmpty(v)	do {\
  if (!((v).size())) { return false; }\
} while (0)

#define assertEquals(aim, x)	do {\
  if ((aim) != (x)) { return false; }\
} while (0)

#define assertNotEquals(aim, x)	do {\
  if ((aim) == (x)) { return false; }\
} while (0)

#define assertExists(aim, v)	do {\
  bool _found = false;\
  for (size_t _i = 0; _i < (v).size() && !_found; ++_i) {\
	if ((aim) == (v).at(_i)) { _found = true; }\
  }\
  if (!_found) { return false; }\
} while (0)

#define assertNotExists(aim, v)	do {\
  for (size_t _i = 0; _i < (v).size(); ++_i) {\
	if ((aim) == (v).at(_i)) { return false; }\
  }\
} while (0)

#define assertThrows(aim, f)	do {\
  try { f; }\
  catch (aim const &) {}\
  catch (...) { return false; }\
} while (0)

#define assertNotThrows(aim, f)	do {\
  try { f; }\
  catch (aim const &) { return false; }\
  catch (...) {}\
} while (0)

#define assertThrowsAny(f)	do {\
  bool _throws = false;\
  try { f; }\
  catch (...) { _throws = true; }\
  if (!_throws) { return false; }\
} while (0)

#define assertNotThrowsAny(f)	do {\
  try { f; }\
  catch (...) { return false; }\
} while (0)

#endif
ile (0)

#define assertThrows(aim, f)	do {\
  try { f; }\
  catch (aim const &) {}\
  catch (...) { return false; }\
} while (0)

#define assertNotThrows(aim, f)	do {\
  try { f; }\
  catch (aim const &) { return false; }\
  catch (...) {}\
} while (0)

#define assertThrowsAny(f)	do {\
  bool _throws = false;\
  try { f; }\
  catch (...) { _throws = true; }\
  if (!_throws) { return false; }\
} while (0)

#define assertNotThrowsAny(f)	do {\
  try { f; 