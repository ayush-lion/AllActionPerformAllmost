package com.app.integrated.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import com.app.abacus.panel.AbacusPanel;
import com.app.abacus.panel.exception.AbacusException;
import com.app.callouts.panel.MainPanel;

import com.app.instructions.beans.Action;
import com.app.sound.SpeechController;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class Performer implements Runnable {

	LinkedHashMap<String, HashMap<String, List<Action>>> data;

	private Thread readerThread;
	MainPanel instructionPanel;
	AbacusPanel abacusPanel;
	private SpeechController playSound;
	private static final String VOICE = "kevin16";
	private VoiceManager vm = null;
	private Voice voice = null;
	private boolean isPlayRobotics;

	/**
	 * @return the isPlayRobotics
	 */
	public boolean isPlayRobotics() {
		return isPlayRobotics;
	}

	/**
	 * @param isPlayRobotics
	 *            the isPlayRobotics to set
	 */
	public void setPlayRobotics(boolean isPlayRobotics) {
		this.isPlayRobotics = isPlayRobotics;
	}

	/**
	 * @return the isPlayNatural
	 */
	public boolean isPlayNatural() {
		return isPlayNatural;
	}

	/**
	 * @param isPlayNatural
	 *            the isPlayNatural to set
	 */
	public void setPlayNatural(boolean isPlayNatural) {
		this.isPlayNatural = isPlayNatural;
	}

	private boolean isPlayNatural;

	public Performer() {
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		isPlayRobotics = false;
		isPlayNatural = false;

	}

	/**
	 * @return the instructionPanel
	 */
	public MainPanel getInstructionPanel() {
		return instructionPanel;
	}

	/**
	 * @param instructionPanel
	 *            the instructionPanel to set
	 */
	public void setInstructionPanel(MainPanel instructionPanel) {
		this.instructionPanel = instructionPanel;
	}

	/**
	 * @return the abacusPanel
	 */
	public AbacusPanel getAbacusPanel() {
		return abacusPanel;
	}

	/**
	 * @param abacusPanel
	 *            the abacusPanel to set
	 */
	public void setAbacusPanel(AbacusPanel abacusPanel) {
		this.abacusPanel = abacusPanel;
	}

	/**
	 * @return the data
	 */
	public LinkedHashMap<String, HashMap<String, List<Action>>> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(LinkedHashMap<String, HashMap<String, List<Action>>> data) {
		this.data = data;
	}

	public void startReading() {
		readerThread = new Thread(this);
		readerThread.start();
	}

	private void playRoboticsVoice(String insTxt) {
		String test = insTxt.replaceAll("\\<.*?>", "");
		String[] txt = insTxt.split("\n");
		String txtInput = "";
		for (String data : txt) {
			if (!data.trim().equalsIgnoreCase("")) {
				txtInput = txtInput + data + " ";
			}
		}
		try {
			/** Setting up voice manager */
			vm = VoiceManager.getInstance();
			voice = vm.getVoice("kevin16");
			voice.allocate();
			voice.speak(txtInput);
			voice.deallocate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// setDataReadyForRead(false);
		}
	}

	private void playText(String insTxt, int counter) {
		String[] txt = insTxt.split("\n");
		String txtInput = "";
		for (String data : txt) {
			if (!data.trim().equalsIgnoreCase("")) {
				txtInput = txtInput + data + " \\pau=1000\\ ";
			}
		}

		playSound = new SpeechController();
		try {
			// playSound.Speak("sharon22k", txtInput);
			playSound.playSound("/audio/" + counter + ".wav");
			// setDataReadyForRead(false);
		} catch (IllegalArgumentException e1) { /** Eating exceptions */
		} catch (IllegalStateException e1) {
			/** Eating exceptions */
		}
	}

	/**
	 * @return the playSound
	 */
	public SpeechController getPlaySound() {
		return playSound;
	}

	/**
	 * @param playSound
	 *            the playSound to set
	 */
	public void setPlaySound(SpeechController playSound) {
		this.playSound = playSound;
	}

	public void stopPlayback() {
		if (readerThread.isAlive()) {
			readerThread.stop();
			if (getPlaySound() != null && getPlaySound().getClip() != null) {
				getPlaySound().stopClip();
			}

		}
	}

	public void start_instructing() throws AbacusException, InterruptedException {
		Set<Entry<String, HashMap<String, List<Action>>>> entrySet = data.entrySet();
		int i = 0;
		ArrayList<String> temp = new ArrayList<String>();
		for (Entry<String, HashMap<String, List<Action>>> entry : entrySet) {
			Object[] tableRow = new Object[3];
			String key = entry.getKey();
			tableRow[0] = key;

			HashMap<String, List<Action>> map = entry.getValue();
			Set<Entry<String, List<Action>>> sEntry = map.entrySet();

			for (Entry<String, List<Action>> entry2 : sEntry) {

				String instruction = entry2.getKey();

				System.out.println("this is instruction" + i);
				if (instruction.isEmpty() || instruction == null) {
					System.out.println("empty");
				} else {

					i++;
					temp.add(instruction);

					if (temp.size() < 3) {
						ArrayList<String> tosend = temp;
						try {
							Thread.sleep(1000);
							// .setData(tosend);
							instructionPanel.ChangeInstructions(tosend, instructionPanel);

							instructionPanel.repaint();

							// validateTree();
							instructionPanel.revalidate();
							instructionPanel.validate();

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						List<String> tosend = temp.subList(temp.size() - 3, temp.size());
						try {
							Thread.sleep(1000);
							instructionPanel.ChangeInstructions(tosend, instructionPanel);

							instructionPanel.repaint();

							// validateTree();
							instructionPanel.revalidate();
							instructionPanel.validate();

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					playRoboticsVoice(instruction.substring(2));
					System.out.println("voice " + i + " " + instruction.substring(2));

				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				List<Action> listOfActions = entry2.getValue();
				StringBuffer actBuf = new StringBuffer();
				for (Action action : listOfActions) {
					System.out.println("actions" + action);

					if (action.getActionName().contains("HighlightFrame")) {
						abacusPanel.highlightFrame();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (action.getActionName().contains("testing wait command")) {

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (action.getActionName().contains("HighlightUpperBeads")) {
						abacusPanel.highlightUpperBeads();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (action.getActionName().contains("HighlightLowerBeads")) {
						abacusPanel.highlightLowerBeads();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (action.getActionName().contains("HighlightRods")) {
						abacusPanel.highlightRods();
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (action.getActionName().contains("HighlightRod")) {
						if (action.getRodNumber() != null && action.getBeadNumber() != null && action.getFinger() == ("useleftpointer")) {
							abacusPanel.highlightSpecificBead(action.getRodNumber(), action.getBeadNumber());
							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else if (action.getRodNumber() != null && action.getBeadNumber() != null && action.getFinger() == ("userightpointer")) {
							abacusPanel.highlightSpecificRod(action.getRodNumber());
							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						else if (action.getRodNumber() != null && action.getBeadNumber() != null) {

							abacusPanel.highlightSpecificBead(action.getRodNumber(), action.getBeadNumber());
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else if (action.getRodNumber() != null) {
							abacusPanel.highlightSpecificRod(action.getRodNumber());
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} 
					else if (action.getActionName().contains("HidePlaceValue")) {
						abacusPanel.resetAllBooleans();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else if (action.getActionName().contains("AddRod")) {
						if (action.getRodNumber() != null && action.getBeadNumber() != null && action.getFinger() == null) {
							abacusPanel.moveEarthBeadUp(action.getRodNumber(), action.getBeadNumber(), "yes");
							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else if (action.getRodNumber() != null && action.getBeadNumber() != null
								&& action.getFinger() != null) {
							abacusPanel.moveEarthBeadUp(action.getRodNumber(), action.getBeadNumber(),
									action.getFinger());

							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						} else if (action.getRodNumber() != null && action.getBeadNumber() != null) {
							abacusPanel.moveEarthBeadUp(action.getRodNumber(), action.getBeadNumber(),
									action.getFinger());
							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else if (action.getActionName().contains("MinusRod")) {
						if (action.getRodNumber() != null && action.getBeadNumber() != null
								&& action.getFinger() == null) {
							abacusPanel.moveEarthBeadDown(action.getRodNumber(), action.getBeadNumber(), "yes");

							abacusPanel.hideFingers(false);

						} else if (action.getRodNumber() != null && action.getBeadNumber() != null
								&& action.getFinger() != null) {
							abacusPanel.moveEarthBeadDown(action.getRodNumber(), action.getBeadNumber(),
									action.getFinger());

							abacusPanel.hideFingers(false);

						} else if (action.getRodNumber() != null && action.getBeadNumber() != null) {
							abacusPanel.moveEarthBeadDown(action.getRodNumber(), action.getBeadNumber(),action.getFinger());
						
							abacusPanel.hideFingers(false);
						}
					} 
					
					else if (action.getActionName().contains("BlinkRod")) {
						
						abacusPanel.resetAllBooleans();
						
						if (action.getRodNumber() != null && action.getBeadNumber() != null && action.getFinger() == null) {
							abacusPanel.startBlinkBead(action.getRodNumber(),action.getBeadNumber());
							Thread.sleep(2000);


						} else if (action.getRodNumber() != null && action.getBeadNumber() != null && action.getFinger() !=null) {
							abacusPanel.startBlinkBead(action.getRodNumber(),action.getBeadNumber());
							Thread.sleep(2000);


						}
						if (action.getRodNumber() != null && action.getBeadNumber() == null) {
							abacusPanel.startBlinkRod(action.getRodNumber());
							Thread.sleep(2000);

						}
					}else if (action.getActionName().contains("HighLightDots")) {
						
					} 
					
					else if (action.getActionName().contains("StopBlink")) {
						if(action.getRodNumber() != null && action.getBeadNumber() != null)
						abacusPanel.stopBlinkRod();
						abacusPanel.stopBlinkBead();
					}
					
					else if (action.getActionName().contains("StopBlink")) {
						if(action.getRodNumber() != null && action.getBeadNumber() != null)
						abacusPanel.stopBlinkRod();
						abacusPanel.stopBlinkBead();
					} 
					
					else if(action.getActionName().contains("HideAllLabels")) {	
						
					}
					else if(action.getActionName().contains("reset")) {	
						abacusPanel.resetAllBooleans();
					}
					
					else if(action.getActionName().contains("AddRod")) {	
						if (action.getRodNumber() != null && action.getBeadNumber() != null && action.getFinger() == null) {
							abacusPanel.moveEarthBeadUp(action.getRodNumber(), action.getBeadNumber(), "yes");
							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else if (action.getRodNumber() != null && action.getBeadNumber() != null && action.getFinger() != null) {
							//abacusPanel. moveBeadUpTogether(action.getRodNumber(), action.getBeadNum, action.getFinger(),action.getFinger());

							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						} else if (action.getRodNumber() != null && action.getBeadNumber() != null) {
							abacusPanel.moveEarthBeadUp(action.getRodNumber(), action.getBeadNumber(),
									action.getFinger());
							abacusPanel.hideFingers(false);
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					
					else if(action.getActionName().contains("Answer")) {	
						
					}
				} 
				
				
				
				
				/*else if (action.getActionName().contains("Rod")) {
				if (action.getRodNumber() != null && action.getBeadNumber() != null	&& action.getFinger() == ("useleftpointer")) {
					abacusPanel.highlightSpecificBead(action.getRodNumber(), action.getBeadNumber());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (action.getRodNumber() != null && action.getBeadNumber() != null) {

					abacusPanel.highlightSpecificBead(action.getRodNumber(), action.getBeadNumber());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (action.getRodNumber() != null) {
					abacusPanel.highlightSpecificRod(action.getRodNumber());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}*/

				/*
				 * 
				 * 
				 * 
				 * else if (action.getActionName().contains("Answer")) {
				 * abacusPanel.displayCount("" + action.getNumber()); try { Thread.sleep(500); }
				 * catch (InterruptedException e) { e.printStackTrace(); }
				 * 
				 * } else if (action.getActionName().contains("Reset")) {
				 * abacusPanel.resetAllBooleans(); try { Thread.sleep(500); } catch
				 * (InterruptedException e) { e.printStackTrace(); }
				 * 
				 * } else if (action.getActionName().contains("Rod")) { if
				 * (action.getBeadNumber() != null) {
				 * abacusPanel.highlightSpecificRod(action.getRodNumber()); //
				 * abacusPanel.hideFingers(false); try { Thread.sleep(500); } catch
				 * (InterruptedException e) { e.printStackTrace(); }
				 * 
				 * } else if (action.getBeadNumber() != null && action.getFinger() != null) { //
				 * abacusPanel.highlightSpecificRod(action.getRodNumber());
				 * abacusPanel.highlightSpecificBead(action.getRodNumber(),
				 * action.getBeadNumber());
				 * 
				 * }
				 * 
				 * }
				 */
			}

		}
	}
	// odel.addRow(tableRow);

	@Override
	public void run() {
		try {
			start_instructing();
		} catch (AbacusException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}

}
