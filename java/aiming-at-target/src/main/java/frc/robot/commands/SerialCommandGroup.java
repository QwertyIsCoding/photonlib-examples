package org.aa8426.robot2023.commands;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;

/**
 * This should be no longer necessary after it is rewritten functional style.
 * 
 * /Oblarg
 * 
 */
@SuppressWarnings("removal")
public class SerialCommandGroup extends CommandGroupBase {
  private final List<Command> m_commands = new ArrayList<>();
  private int m_currentCommandIndex = -1;
  private boolean m_runWhenDisabled = true;
  private InterruptionBehavior m_interruptBehavior = InterruptionBehavior.kCancelIncoming;

  /**
   * Literally a copy paste of Sequential command group but without the fool proofing against
   * using a command in multiple compositions.
   * 
   * Creates a new SequentialCommandGroup. The given commands will be run sequentially, with the
   * composition finishing when the last command finishes.
   *
   * @param commands the commands to include in this composition.
   */
  public SerialCommandGroup(Command... commands) {
    addCommands(commands);
  }

  @Override
  public final void addCommands(Command... commands) {
    if (m_currentCommandIndex != -1) {
      throw new IllegalStateException(
          "Commands cannot be added to a composition while it's running");
    }

    // let's just be adults here.
    //CommandScheduler.getInstance().registerComposedCommands(commands);

    for (Command command : commands) {
      m_commands.add(command);
      m_requirements.addAll(command.getRequirements());
      m_runWhenDisabled &= command.runsWhenDisabled();
      if (command.getInterruptionBehavior() == InterruptionBehavior.kCancelSelf) {
        m_interruptBehavior = InterruptionBehavior.kCancelSelf;
      }
    }
  }

  @Override
  public final void initialize() {
    m_currentCommandIndex = 0;

    if (!m_commands.isEmpty()) {
      m_commands.get(0).initialize();
    }
  }

  @Override
  public final void execute() {
    if (m_commands.isEmpty()) {
      return;
    }

    Command currentCommand = m_commands.get(m_currentCommandIndex);

    currentCommand.execute();
    if (currentCommand.isFinished()) {
      currentCommand.end(false);
      m_currentCommandIndex++;
      if (m_currentCommandIndex < m_commands.size()) {
        m_commands.get(m_currentCommandIndex).initialize();
      }
    }
  }

  @Override
  public final void end(boolean interrupted) {
    if (interrupted
        && !m_commands.isEmpty()
        && m_currentCommandIndex > -1
        && m_currentCommandIndex < m_commands.size()) {
      m_commands.get(m_currentCommandIndex).end(true);
    }
    m_currentCommandIndex = -1;
  }

  @Override
  public final boolean isFinished() {
    return m_currentCommandIndex == m_commands.size();
  }

  @Override
  public boolean runsWhenDisabled() {
    return m_runWhenDisabled;
  }

  @Override
  public InterruptionBehavior getInterruptionBehavior() {
    return m_interruptBehavior;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);

    builder.addIntegerProperty("index", () -> m_currentCommandIndex, null);
  }
}
