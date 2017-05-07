package com.example.officerbumble.gameentities;

import java.util.Random;

import com.example.officerbumble.gameentities.Criminal.CRIMINAL_WEAPON;

// This class is designed to generate attack patterns depending on the various game factors.
public class AttackPatternGenerator {
	private float m_chickenatorAttackPercentage = 0.0f;
	private float m_bowlingBallAttackPercentage = 0.0f;
	private float m_pieAttackPercentage = 0.0f;
	private int m_maxWeaponsAllowedInAttack = 0;
	private int m_level = 0;
	Random rand = new Random();
	
	public AttackPatternGenerator(int _level, int _maxWeaponsAllowedInAttack, float _chickenatorAttackPercentage, float _bowlingBallAttackPercentage, float _pieAttackPercentage) {
		m_level = _level;
		m_chickenatorAttackPercentage = _chickenatorAttackPercentage;
		m_bowlingBallAttackPercentage = _bowlingBallAttackPercentage;
		m_pieAttackPercentage = _pieAttackPercentage;
		m_maxWeaponsAllowedInAttack = _maxWeaponsAllowedInAttack;
	}
		
	public CRIMINAL_WEAPON[] GenerateAttackPattern(boolean _canThrowChickenator) {				
		int numberOfAttacks = GetRandomNumber(1, m_maxWeaponsAllowedInAttack);
		int pattern = GetRandomNumber(1, 27);
		CRIMINAL_WEAPON[] result = null;

		// Attacks are mostly pattern based.
		//
		// Normal Patterns
		// Pattern 1 = All the same weapons in a row.
		// Pattern 2 = Pie, Bowling Ball, repeated (random sequence ordering though).
		// Pattern 3 = Chickenator high, chickenator low, chickenator high, chickenator low.
		// Pattern 4 = All 3 repeated (random sequence ordering though).		
		// Pattern 5 = Bowling Ball, Chickenator High (repeated).
		// Pattern 6 = Bowling Ball, Chickenator Low (repeated).
		// Pattern 7 = Pie, Chickenator High (repeated).
		// Pattern 8 = Pie, Chickenator Low (repeated).
		// Pattern 9 = 1 random item.
		// Pattern 10 = Completely random. 
		
		// Hard Patterns
		// Pattern 11 = Low chickenators (at least 3, or the min), and a high chickenator.
		// Pattern 12 = High chickenators (at least 3, or the min), and a low chickenator.
		// Pattern 13 = Pie, Pie, Bowling Ball, Chickenator High, Chickenator High, Chickenator Low.
		// Pattern 14 = Chickenator High, Chickenator High, Chickenator Low, Chickenator High
		// Pattern 15 = Chickenator Low, Chickenator Low, Chickenator High, Chickenator Low.
		// Pattern 16 = Chickenator High, Chickenator High, Chickenator Low, Chickenator Low repeated.
        // Pattern 19 = Bowling Ball, Chickenator High, Bowling Ball, Chickenator High... Chickenator Low, Chickenator High
        // Pattern 20 = Pie, Chickenator High, Pie, Chickenator High ... Chickenator Low, Chickenator High

    	switch(pattern) {
			case 1:
				result = QueuePattern1(numberOfAttacks, _canThrowChickenator);
				break;
			case 2:
				result = QueuePattern2(numberOfAttacks);
				break;
			case 3:
				result = QueuePattern3(numberOfAttacks, _canThrowChickenator);
				break;
			case 4:
				result = QueuePattern4(numberOfAttacks);
				break;
			case 5:
				result = QueuePattern5(numberOfAttacks, _canThrowChickenator);
				break;
			case 6:
				result = QueuePattern6(numberOfAttacks, _canThrowChickenator);
				break;
			case 7:
				result = QueuePattern7(numberOfAttacks, _canThrowChickenator);
				break;
			case 8:
				result = QueuePattern8(numberOfAttacks, _canThrowChickenator);
				break;
			case 9:
				result = QueuePattern9(_canThrowChickenator);
				break;
			case 10:
				result = QueuePattern10(numberOfAttacks, _canThrowChickenator);
				break;
			case 11:
				result = QueuePattern11(numberOfAttacks, _canThrowChickenator);
				break;
			case 12:
				result = QueuePattern12(numberOfAttacks, _canThrowChickenator);
				break;
			case 13:
				result = QueuePattern13(numberOfAttacks, _canThrowChickenator);
				break;
			case 14:
				result = QueuePattern14(numberOfAttacks, _canThrowChickenator);
				break;
			case 15:
				result = QueuePattern15(numberOfAttacks, _canThrowChickenator);
				break;
			case 16:
				result = QueuePattern16(numberOfAttacks, _canThrowChickenator);
				break;
			case 17:
				result = QueuePattern10(numberOfAttacks, _canThrowChickenator);
				break;			
			case 18:
				result = QueuePattern10(numberOfAttacks, _canThrowChickenator);
				break;
            case 19:
                result = QueuePattern19(numberOfAttacks, _canThrowChickenator);
                break;
            case 20:
                result = QueuePattern20(numberOfAttacks, _canThrowChickenator);
                break;
            default:
                result = QueuePattern10(numberOfAttacks, _canThrowChickenator);
                break;
		}
		
		return result;		
	}
	
	private int GetRandomNumber(int _min, int _max) {		
		return rand.nextInt(_max - _min + 1) + _min;
	}

	// Pattern 1 = All the same weapons in a row.
	private CRIMINAL_WEAPON[] QueuePattern1(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;
		int weapon;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		if (_canThrowChickenator) {
			weapon = GetRandomNumber(1, 3);	  // Chickenator allowed.			
		} else {
			weapon = GetRandomNumber(2, 3);	  // Chickenator not allowed.
		}
								
		while(attackNum < _numberOfAttacks) {
			if(weapon == 1) {				
				int position = GetRandomNumber(1, 2);
				
				if(position == 1) {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;
				}
				else {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;					
				}
			} else if (weapon == 2) {
				queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL;				
			} else {
				queue[attackNum] = CRIMINAL_WEAPON.PIE;
			}
						
			attackNum++;		
		}		
		
		return queue;
	}
	
	// Alternate Pie, Bowling Ball, repeated (random sequence ordering though).
	private CRIMINAL_WEAPON[] QueuePattern2(int _numberOfAttacks) {
		int attackNum = 0;
		int weapon = GetRandomNumber(1, 2);
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		while(attackNum < _numberOfAttacks) {
			if(weapon == 1) {				
				queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL;
				weapon = 2;
			} else {
				queue[attackNum] = CRIMINAL_WEAPON.PIE;
				weapon = 1;
			}
						
			attackNum++;		
		}
		
		return queue;
	}
	
	// Pattern 3 = Chickenator high, chickenator low, chickenator high, chickenator low.
	private CRIMINAL_WEAPON[] QueuePattern3(int _numberOfAttacks, boolean _canThrowChickenator) {		
		int attackNum = 0;
		int weapon = GetRandomNumber(1, 2);
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		if (_canThrowChickenator) {
			while(attackNum < _numberOfAttacks) {
				if(weapon == 1) {				
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;					
					weapon = 2;
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;
					weapon = 1;
				}
							
				attackNum++;		
			}				
		} else {
			// Can't throw chickenators, so we'll fallback to bowling balls and pies.
			queue = QueuePattern2(_numberOfAttacks);
		}	
		
		return queue;
	}

	// Pattern 4 = All 3 repeated (random sequence ordering though).			
	private CRIMINAL_WEAPON[] QueuePattern4(int _numberOfAttacks) {
		int attackNum = 0;		
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];

		int weapon = GetRandomNumber(1, 3);
			
		while(attackNum < _numberOfAttacks) {
			if(weapon == 1) {
				int position = GetRandomNumber(1, 2);
					
				if(position == 1) {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;					
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;					
				}
					
				weapon = 2;
			} else if(weapon == 2) {
				queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL;				
				weapon = 3;
			} else {
				queue[attackNum] = CRIMINAL_WEAPON.PIE;
				weapon = 1;
			}
							
			attackNum++;		
		}			
		
		return queue;
	} 
		
	// Pattern 5 = Bowling Ball, Chickenator High (repeated).
	private CRIMINAL_WEAPON[] QueuePattern5(int _numberOfAttacks, boolean _canThrowChickenator) {
		int weapon = GetRandomNumber(1, 2);
		int attackNum = 0;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		if(_canThrowChickenator) {
			while(attackNum < _numberOfAttacks) {
				if(weapon == 1) {				
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;					
					weapon = 2;
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL;
					weapon = 1;
				}
							
				attackNum++;		
			}							
		} else {
			queue = QueuePattern10(_numberOfAttacks, _canThrowChickenator);
		}
		
		return queue;
	}
	
	// Pattern 6 = Bowling Ball, Chickenator Low (repeated).
	private CRIMINAL_WEAPON[] QueuePattern6(int _numberOfAttacks, boolean _canThrowChickenator) {
		int weapon = GetRandomNumber(1, 2);
		int attackNum = 0;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];

		if(_canThrowChickenator) {
			while(attackNum < _numberOfAttacks) {
				if(weapon == 1) {				
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;					
					weapon = 2;
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL;					
					weapon = 1;
				}
							
				attackNum++;		
			}							
		} else {
			queue = QueuePattern10(_numberOfAttacks, _canThrowChickenator);
		}		
		
		return queue;
	}
	
	// Pattern 7 = Pie, Chickenator High (repeated).
	private CRIMINAL_WEAPON[] QueuePattern7(int _numberOfAttacks, boolean _canThrowChickenator) {
		int weapon = GetRandomNumber(1, 2);
		int attackNum = 0;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];

		if(_canThrowChickenator) {
			while(attackNum < _numberOfAttacks) {
				if(weapon == 1) {				
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;					
					weapon = 2;
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.PIE;					
					weapon = 1;
				}
							
				attackNum++;		
			}							
		} else {
			queue = QueuePattern10(_numberOfAttacks, _canThrowChickenator);
		}		
		
		return queue;
	}

	// Pattern 8 = Pie, Chickenator Low (repeated).
	private CRIMINAL_WEAPON[] QueuePattern8(int _numberOfAttacks, boolean _canThrowChickenator) {
		int weapon = GetRandomNumber(1, 2);
		int attackNum = 0;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];

		if(_canThrowChickenator) {
			while(attackNum < _numberOfAttacks) {
				if(weapon == 1) {				
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;				
					weapon = 2;
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.PIE;
					weapon = 1;
				}
							
				attackNum++;		
			}							
		} else {
			queue = QueuePattern10(_numberOfAttacks, _canThrowChickenator);
		}		
		
		return queue;
	}	
	
	// Pattern 9: 1 Random Item only.
	private CRIMINAL_WEAPON[] QueuePattern9(boolean _canThrowChickenator) {
		return QueuePattern10(1, _canThrowChickenator);
	}
	
	// Pattern 10: Completely Random.
	private CRIMINAL_WEAPON[] QueuePattern10(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;
		float attackPercentage = 0;
		float chickenatorAttackPercentage = m_chickenatorAttackPercentage + 0.001f;  // Adding a small number so we never accidentally hit the chickenator if it's disabled. 
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		while(attackNum < _numberOfAttacks) {									
			if(_canThrowChickenator) {
				attackPercentage = rand.nextFloat() * (1.0f - 0.0f) + 0.0f;
			} else {
				attackPercentage = rand.nextFloat() * (1.0f - chickenatorAttackPercentage) + chickenatorAttackPercentage;
			}
				
			if(attackPercentage <= m_chickenatorAttackPercentage) {
				int position = GetRandomNumber(1, 2);
				if(position == 1) {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;					
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;					
				}				
			} else if (attackPercentage > m_chickenatorAttackPercentage && attackPercentage <= m_chickenatorAttackPercentage + m_bowlingBallAttackPercentage) {
				queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL;				
			} else {
				queue[attackNum] = CRIMINAL_WEAPON.PIE;
			}
			
			attackNum++;		
		}			
		
		return queue;
	}
	
	// Pattern 11 = Low chickenators, a high chickenator, and a low chickenator.
	private CRIMINAL_WEAPON[] QueuePattern11(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];

		if(_numberOfAttacks == 1) {
			return QueuePattern9(_canThrowChickenator);
		} else if (!_canThrowChickenator) {
			return QueuePattern10(_numberOfAttacks, _canThrowChickenator);
		} else {
			while(attackNum < _numberOfAttacks) {
				if(attackNum == 1) {		// Second Last one.		
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;										
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;
				}
								
				attackNum++;		
			}							
		}
				
		return queue;
	}	

	// Pattern 12 High chickenators (at least 3, or the min), and a low chickenator.
	private CRIMINAL_WEAPON[] QueuePattern12(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];

		if(_numberOfAttacks == 1) {
			return QueuePattern9(_canThrowChickenator);
		} else if (!_canThrowChickenator) {
			return QueuePattern10(_numberOfAttacks, _canThrowChickenator);
		} else {
			while(attackNum < _numberOfAttacks) {
				if(attackNum == 1) {		// Second Last one.		
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;										
				} else {
					queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;
				}
								
				attackNum++;		
			}							
		}
				
		return queue;
	}
	
	// Pattern 13 = Pie, Pie, Bowling Ball, Chickenator High, Chickenator High, Chickenator Low.
	private CRIMINAL_WEAPON[] QueuePattern13(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;

		if(_numberOfAttacks > 6) {
			_numberOfAttacks = 6;
		}
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		if(_numberOfAttacks == 1) {
			return QueuePattern9(_canThrowChickenator);		
		} else {
			while(attackNum < _numberOfAttacks) {
				switch(attackNum) {
					case 0: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_LOW : CRIMINAL_WEAPON.BOWLING_BALL); 
							break;
					case 1: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_HIGH : CRIMINAL_WEAPON.PIE); 
							break;
					case 2: queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH; 
							break;
					case 3: queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL; 
							break;
					case 4: queue[attackNum] = CRIMINAL_WEAPON.PIE; 
							break;
					case 5: queue[attackNum] = CRIMINAL_WEAPON.PIE; 
							break;												
				}
								
				attackNum++;		
			}							
		}
				
		return queue;
	}
	
	// Pattern 14 = Chickenator High, Chickenator High, Chickenator Low, Chickenator High
	private CRIMINAL_WEAPON[] QueuePattern14(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;		

		if(_numberOfAttacks > 4) {
			_numberOfAttacks = 4;
		}
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		if(_numberOfAttacks == 1) {
			return QueuePattern9(_canThrowChickenator);		
		} else {
			while(attackNum < _numberOfAttacks) {
				switch(attackNum) {
					case 0: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_HIGH : CRIMINAL_WEAPON.PIE); 
							break;
					case 1: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_LOW : CRIMINAL_WEAPON.BOWLING_BALL); 
							break;
					case 2: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_HIGH : CRIMINAL_WEAPON.PIE); 
							break;
					case 3: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_HIGH: CRIMINAL_WEAPON.PIE); 
							break;
				}
								
				attackNum++;		
			}							
		}
				
		return queue;
	}
	
	// Pattern 15 = Chickenator Low, Chickenator Low, Chickenator High, Chickenator Low.
	private CRIMINAL_WEAPON[] QueuePattern15(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;		

		if(_numberOfAttacks > 4) {
			_numberOfAttacks = 4;
		}
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		if(_numberOfAttacks == 1) {
			return QueuePattern9(_canThrowChickenator);		
		} else {
			while(attackNum < _numberOfAttacks) {
				switch(attackNum) {
					case 0: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_LOW : CRIMINAL_WEAPON.BOWLING_BALL); 
							break;
					case 1: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_HIGH : CRIMINAL_WEAPON.PIE); 
							break;
					case 2: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_LOW : CRIMINAL_WEAPON.BOWLING_BALL); 
							break;
					case 3: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_LOW: CRIMINAL_WEAPON.BOWLING_BALL); 
							break;
				}
								
				attackNum++;		
			}							
		}
				
		return queue;
	}
	
	// Pattern 16 = Chickenator High, Chickenator High, Chickenator Low, Chickenator Low repeated.
	private CRIMINAL_WEAPON[] QueuePattern16(int _numberOfAttacks, boolean _canThrowChickenator) {
		int attackNum = 0;
		CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[_numberOfAttacks];
		
		if(_numberOfAttacks == 1) {
			return QueuePattern9(_canThrowChickenator);		
		} else {
			while(attackNum < _numberOfAttacks) {
				switch(attackNum % 4) {
					case 0: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_LOW : CRIMINAL_WEAPON.BOWLING_BALL); 
							break;
					case 1: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_LOW : CRIMINAL_WEAPON.BOWLING_BALL); 
							break;
					case 2: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_HIGH : CRIMINAL_WEAPON.PIE); 
							break;
					case 3: queue[attackNum] = (_canThrowChickenator ? CRIMINAL_WEAPON.CHICKENATOR_HIGH: CRIMINAL_WEAPON.PIE); 
							break;
				}
								
				attackNum++;		
			}							
		}
				
		return queue;
	}

    // Pattern 19 = Bowling Ball, Chickenator High, Bowling Ball, Chickenator High... Chickenator Low, Chickenator High
    private CRIMINAL_WEAPON[] QueuePattern19(int numberOfAttacks, boolean canThrowChickenator)  {
        int attackNum = 0;
        int numAttacks = numberOfAttacks;

        if(numAttacks <= 5) {
            numAttacks = 6;
        }
        CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[numAttacks];

        if(numAttacks == 1) {
            return QueuePattern9(canThrowChickenator);
        } else if (!canThrowChickenator) {
            return QueuePattern10(numberOfAttacks, canThrowChickenator);
        } else {
            while(attackNum < numAttacks) {
                if (attackNum == 0) {
                    queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;
                } else if(attackNum == 1) {		// Second Last one.
                    queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;
                } else {
                    if ( (attackNum + 1) % 2 == 1) {
                        queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;
                    } else {
                        queue[attackNum] = CRIMINAL_WEAPON.BOWLING_BALL;
                    }
                }

                attackNum = attackNum + 1;
            }
        }

        return queue;
    }

    // Pattern 20 = Pie, Chickenator High, Pie, Chickenator High ... Chickenator Low, Chickenator High
    private CRIMINAL_WEAPON[] QueuePattern20(int numberOfAttacks, boolean canThrowChickenator) {
        int attackNum = 0;
        int numAttacks = numberOfAttacks;

        if(numAttacks <= 5) {
            numAttacks = 6;
        }
        CRIMINAL_WEAPON[] queue = new CRIMINAL_WEAPON[numAttacks];

        if(numAttacks == 1) {
            return QueuePattern9(canThrowChickenator);
        } else if (!canThrowChickenator) {
            return QueuePattern10(numberOfAttacks, canThrowChickenator);
        } else {
            while(attackNum < numAttacks) {
                if (attackNum == 0) {
                    queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;
                } else if(attackNum == 1) {		// Second Last one.
                    queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_HIGH;
                } else {
                    if ( (attackNum + 1) % 2 == 1) {
                        queue[attackNum] = CRIMINAL_WEAPON.CHICKENATOR_LOW;
                    } else {
                        queue[attackNum] = CRIMINAL_WEAPON.PIE;
                    }
                }

                attackNum = attackNum + 1;
            }
        }

        return queue;
    }
	
}
